package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.application.fegin.HubClient;
import com.nuttty.eureka.order.application.fegin.dto.ContentDto;
import com.nuttty.eureka.order.application.fegin.dto.DirectionsResponse;
import com.nuttty.eureka.order.application.fegin.dto.HubDto;
import com.nuttty.eureka.order.application.fegin.dto.WayPoint;
import com.nuttty.eureka.order.domain.model.HubRoute;
import com.nuttty.eureka.order.infrastructure.repository.HubRouteRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j(topic = "HubRouteService")
@RequiredArgsConstructor
public class HubRouteService {
    private final HubClient hubClient;
    private final HubRouteRepository hubRouteRepository;
    private final NaverDirectionService naverDirectionService;

    // 보상 트랜잭션에 필요한 경로들을 추적할 리스트
    private final List<HubRoute> successfullyCreatedRoutes = new ArrayList<>();

    @Transactional
    public void createAllHubRoutes() {
        try {
            // 허브 목록 조회
            List<ContentDto> content = hubClient.getAllHubs("19092").getContent();
            List<HubDto> hubDtos = content.get(0).getHubDto();
            log.info("허브 목록 조회 | size: {}", hubDtos.size());

            // 인접 허브 간 이동 경로 저장
            for (int i = 0; i < hubDtos.size() - 1; i++) {
                HubDto currentHub = hubDtos.get(i);
                HubDto nextHub = hubDtos.get(i + 1);
                log.info("현재 허브: {}, 다음 허브: {}", currentHub.getName(), nextHub.getName());

                // 두 허브 간 양방향 이동 경로 생성 (A -> B, B -> A)
                // Resilience4j @Retry 적용
                boolean hubRoute = createHubRoute(currentHub, nextHub);
                boolean hubRouteReverse = createHubRoute(nextHub, currentHub);

                // 허브 간 경로 실패시 정합성 유지를 위해 보상 트랜잭션 필요
                if (!hubRoute || !hubRouteReverse) {
                    log.error("허브 경로 생성 실패 | currentHub: {}, nextHub: {}", currentHub.getName(), nextHub.getName());

                    // 보상 트랜잭션
                    performCompensation(); // 실패 시 보상 트랜잭션 실행
                }
            }
        } catch (Exception e) {
            log.error("허브 경로 생성 중 오류 발생 | {}", e.getMessage());
        }

    }

    private void performCompensation() {
        log.info("보상 트랜잭션 실행: 성공적으로 생성된 경로들 롤백 중...");
        for (HubRoute route : successfullyCreatedRoutes) {
            try {
                hubRouteRepository.delete(route);  // 성공적으로 생성된 경로 롤백
                log.info("경로 롤백 성공 | departureHub: {}, arrivalHub: {}", route.getDepartureHubId(), route.getArrivalHubId());
            } catch (Exception e) {
                log.error("경로 롤백 실패 | departureHub: {}, arrivalHub: {}", route.getDepartureHubId(), route.getArrivalHubId(), e);
            }
        }
        successfullyCreatedRoutes.clear(); // 롤백 완료 후 리스트 초기화
    }

    // Resilience4j @Retry 적용
    // 3회까지 재시도, 재시도 실패 시 handleCreateHubRouteFailure 메서드 호출
    @Retry(name = "createHubRoute", fallbackMethod = "handleCreateHubRouteFailure")
    private boolean createHubRoute(HubDto departureHub, HubDto arrivalHub) {
        try {
            // Direction 5 API 호출하여 소요시간 및 거리 계산
            DirectionsResponse directions = naverDirectionService.getDirecitons(Arrays.asList(
                    new WayPoint(departureHub.getLongitude(), departureHub.getLatitude()),
                    new WayPoint(arrivalHub.getLongitude(), arrivalHub.getLatitude())
            ));

            // 소요 시간 및 거리 정보 추출
            Double distanceInKilometers = directions.getRoute().getTraoptimal().get(0).getSummary().getDistance();
            Double durationInSeconds = directions.getRoute().getTraoptimal().get(0).getSummary().getDuration();

            // 허브 간 경로 생성
            String routeInfo = departureHub.getName() + " -> " + arrivalHub.getName();

            log.info("출발 허브 ID: {}, 도착 허브 ID: {}, 경로 정보: {}", departureHub.getHubId(), arrivalHub.getHubId(), routeInfo);
            HubRoute hubRoute = HubRoute.create(
                    departureHub.getHubId(),
                    arrivalHub.getHubId(),
                    durationInSeconds,
                    distanceInKilometers,
                    routeInfo
            );

            // DB에 저장
            hubRouteRepository.save(hubRoute);
            // 성공적으로 생성된 경로 리스트에 추가
            successfullyCreatedRoutes.add(hubRoute);

            return true;
        } catch (Exception e) {
            log.error("Direction 5 허브 경로 생성 중 오류 발생 | departureHub: {}, arrivalHub: {}", departureHub.getName(), arrivalHub.getName());
            throw e; // 예외 발생 시 Resilience4j가 재시도
        }

    }

    // createHubRoute 메서드 실패 시 호출되는 메서드
    private boolean handleCreateHubRouteFailure(HubDto departureHub, HubDto arrivalHub, Exception e) {
        log.error("허브 경로 생성 실패 | departureHub: {}, arrivalHub: {}", departureHub.getName(), arrivalHub.getName());
        return false;
    }


    // 허브 간 경로 조회(전체 - DFS 방식으로 경로 탐색)
    @Transactional(readOnly = true)
    public List<HubRoute> findAllHubRoutes(UUID startHubId, UUID endHubId) {
        // 경로 저장할 리스트
        List<HubRoute> routes = new ArrayList<>();

        // 방문한 허브를 추적하기
        Set<UUID> visitedHubs = new HashSet<>();

        // DFS로 경로 탐색
        if (findRouteDFS(startHubId, endHubId, visitedHubs, routes)) {
            Collections.reverse(routes);
            // 경로를 찾으면 리스트 반환
            return routes;
        } else {
            // 경로를 찾지 못하면 빈 리스트 반환
            return Collections.emptyList();
        }
    }

    private boolean findRouteDFS(UUID currentHubId, UUID endHubId, Set<UUID> visitedHubs, List<HubRoute> routes) {
        // 도착 허브에 도달한 경우 true 반환
        if (currentHubId.equals(endHubId)) {
            return true;
        }

        // 이미 방문한 허브라면 다시 방문 하지않음.
        if (visitedHubs.contains(currentHubId)) {
            return false;
        }

        // 현재 허브를 방문한 허브로 추가
        visitedHubs.add(currentHubId);

        // 현재 허브에서 이동 가능한 허브 목록 조회
        List<HubRoute> hubRoutes = hubRouteRepository.findByDepartureHubId(currentHubId);

        for (HubRoute hubRoute : hubRoutes) {
            // 다음 허브로 이동
            if (findRouteDFS(hubRoute.getArrivalHubId(), endHubId, visitedHubs, routes)) {
                // 경로 추가
                routes.add(hubRoute);
                return true;
            }
        }

        return false;
    }
}