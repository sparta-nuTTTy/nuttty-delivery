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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j(topic = "HubRouteService")
@RequiredArgsConstructor
public class HubRouteService {
    private final HubClient hubClient;
    private final HubRouteRepository hubRouteRepository;

    // 허브 경로 생성
    // 허브 경로는 허브 목록을 조회하여 각 객체를 리스트로 저장
    // 허브간 이동 정보 테이블(p_hub_route)에 저장
    @Transactional
    public void createAllHubRoutes() {
        // 허브 목록 조회
        List<ContentDto> content = hubClient.getAllHubs().getContent();
        List<HubDto> hubDto = content.get(0).getHubDto();

        // 인접 허브 간 이동 경로 저장
        for (int i = 0; i < hubDto.size() - 1; i++) {
            HubDto currentHub = hubDto.get(i);
            HubDto nextHub = hubDto.get(i + 1);

            // 두 허브 간 이동 경로 생성
            createHubRoute(currentHub, nextHub);
            createHubRoute(nextHub, currentHub);
        }

    }

    private void createHubRoute(HubDto departureHub, HubDto arrivalHub) {
        // 허브 간 경로 생성
        String routeInfo  = departureHub.getName() + " -> " + arrivalHub.getName();

        HubRoute hubRoute = HubRoute.create(
                departureHub.getHubId(),
                arrivalHub.getHubId(),
                LocalDateTime.now(), // 추후 네이버 Map API 중 Directions 5 API 연동하여 시간 계산
                routeInfo
        );

        // DB에 저장
        hubRouteRepository.save(hubRoute);

    }

    // 허브 간 경로 조회(전체 - 링크드 리스트)
    @Transactional(readOnly = true)
    public List<HubRoute> findAllHubRoutes(UUID starHubId, UUID endHubId) {
        List<HubRoute> routes = new ArrayList<>();

        UUID currentHubId = starHubId;

        while (!currentHubId.equals(endHubId)) {
            // 출발 허브부터 도착 허브까지 경로 조회
            HubRoute route = hubRouteRepository.findByDepartureHubId(currentHubId);
            routes.add(route);

            // 다음 허브로 이동
            currentHubId = route.getArrivalHubId();
        }

        return routes;
    }
}