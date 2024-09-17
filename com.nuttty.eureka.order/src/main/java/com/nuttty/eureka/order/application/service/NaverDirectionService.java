package com.nuttty.eureka.order.application.service;

import com.nuttty.eureka.order.application.fegin.dto.DirectionsResponse;
import com.nuttty.eureka.order.application.fegin.dto.WayPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j(topic = "NaverDirectionService")
@RequiredArgsConstructor
public class NaverDirectionService {
    private final RestTemplate restTemplate;

    private static final String DIRECTIONS_URL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving";

    @Value("${naver.direction.client-id}")
    private String clientId;

    @Value("${naver.direction.client-secret}")
    private String clientSecret;

    public DirectionsResponse getDirecitons(List<WayPoint> wayPoints) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        String url = buildUrl(wayPoints);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<DirectionsResponse> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, DirectionsResponse.class);

        return exchange.getBody();
    }

    // 네이버 Direction5 API 호출 URL 생성
    private String buildUrl(List<WayPoint> waypoints) {
        // toString 메서드를 재정의하여 경도, 위도 순서로 반환
        String start = waypoints.get(0).toString();
        log.info("start: {}", start);
        String goal = waypoints.get(waypoints.size() - 1).toString();
        log.info("goal: {}", goal);
        StringBuilder waypointsParam = new StringBuilder();

        // 경유지가 있을 경우 추가
        if (waypoints.size() > 2) {
            for (int i = 1; i < waypoints.size() - 1; i++) {
                waypointsParam.append(waypoints.get(i).toString());
                if (i < waypoints.size() - 2) {
                    waypointsParam.append(":");
                }
            }
        }

        String result = DIRECTIONS_URL + "?start=" + start + "&goal=" + goal + "&waypoints=" + waypointsParam.toString() + "&option=";
        log.info("url: {}", result);
        return result;
    }
}
