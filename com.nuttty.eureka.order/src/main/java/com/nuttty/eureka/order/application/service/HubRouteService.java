package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.application.fegin.HubClient;
import com.nuttty.eureka.order.application.fegin.dto.ContentDto;
import com.nuttty.eureka.order.application.fegin.dto.HubDto;
import com.nuttty.eureka.order.domain.model.HubRoute;
import com.nuttty.eureka.order.infrastructure.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j(topic = "HubRouteService")
@RequiredArgsConstructor
public class HubRouteService {
    private final HubClient hubClient;
    private final HubRouteRepository hubRouteRepository;

    @Transactional
    public void createAllHubRoutes() {
        // 허브 목록 조회
        List<ContentDto> content = hubClient.getAllHubs("19092").getContent();
        List<HubDto> hubDtos = content.get(0).getHubDto();
        log.info("허브 목록 조회 | size: {}", hubDtos.size());

        // 인접 허브 간 이동 경로 저장
        for (int i = 0; i < hubDtos.size() - 1; i++) {
            HubDto currentHub = hubDtos.get(i);
            HubDto nextHub = hubDtos.get(i + 1);
            log.info("현재 허브: {}, 다음 허브: {}", currentHub.getName(), nextHub.getName());

            // 두 허브 간 이동 경로 생성
            createHubRoute(currentHub, nextHub);
            createHubRoute(nextHub, currentHub);
        }

    }

    private void createHubRoute(HubDto departureHub, HubDto arrivalHub) {
        // 허브 간 경로 생성
        String routeInfo  = departureHub.getName() + " -> " + arrivalHub.getName();

        log.info("출발 허브 ID: {}, 도착 허브 ID: {}, 경로 정보: {}", departureHub.getHubId(), arrivalHub.getHubId(), routeInfo);
        HubRoute hubRoute = HubRoute.create(
                departureHub.getHubId(),
                arrivalHub.getHubId(),
                LocalDateTime.now(), // 추후 네이버 Map API 중 Directions 5 API 연동하여 시간 계산
                routeInfo
        );

        // DB에 저장
        hubRouteRepository.save(hubRoute);

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