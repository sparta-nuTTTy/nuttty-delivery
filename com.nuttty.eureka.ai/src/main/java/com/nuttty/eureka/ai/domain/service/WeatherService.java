package com.nuttty.eureka.ai.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuttty.eureka.ai.application.dto.hub.HubDto;
import com.nuttty.eureka.ai.application.dto.weather.WeatherSummary;
import com.nuttty.eureka.ai.config.JsonFormatter;
import com.nuttty.eureka.ai.exception.exceptionsdefined.WeatherException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {

    private final RestTemplate restTemplate;

    /**
     * 기상청 API 호출
     * 허브 별 날씨 정보
     */
    public Map<UUID, String> wheather(List<HubDto> findHubList) throws URISyntaxException {

        // 오늘 날짜 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String format = LocalDate.now().format(formatter);

        // 허브 별 날씨 정보
        Map<UUID, String> weatherMap = new HashMap<>();

        for (HubDto hubDto : findHubList) {
            UUID hubId = hubDto.getHubId();
            String hubName = hubDto.getName();

            URI uri = new URI("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
                    + "?serviceKey=nVQctOrihiA8%2BchYC1W803syMiJOHkuS%2BUfRixt1K7bHkyWTeXZI%2BGe80OfJCieIoPCeVtz9P0BCM6BISqskHw%3D%3D"
                    + "&pageNo=1"
                    + "&numOfRows=1000"
                    + "&dataType=JSON"
                    + "&base_date=" + 20240919
                    + "&base_time=0600"
                    + "&nx=" + hubDto.getLatitude()
                    + "&ny=" + hubDto.getLongitude());

            // GET 요청 보내기
            ResponseEntity<String> response = null;
            try {
                response = restTemplate.getForEntity(uri, String.class);
            } catch (RestClientException e) {
                throw new WeatherException(e.getMessage());
            }

            // JSON 응답을 포맷팅
            String formattedJson = JsonFormatter.formatJson(response.getBody());

            // 포맷한 데이터를 한국어로 변경
            String summary = weatherJsonParse(hubName, formattedJson);

            weatherMap.computeIfAbsent(hubId, k -> summary);
        }

        return weatherMap;
    }

    /**
     * 기상청 날씨 JSON 데이터 파싱
     * @param hubName
     * @param formattedJson
     * @return
     */
    private String weatherJsonParse(String hubName, String formattedJson) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(formattedJson);


            // 필요한 데이터 추출
            JsonNode items = root.path("response").path("body").path("items").path("item");

            // 각 데이터를 요약
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (JsonNode item : items) {
                Map<String, Object> map = new HashMap<>();
                map.put("category", item.get("category").asText());
                map.put("obsrValue", item.get("obsrValue").asText());
                itemList.add(map);
            }

            WeatherSummary summary = summarizeWeather(hubName, itemList);

            return summary.toString();
        } catch (Exception e) {
            log.error("JSON parsing error", e);
            throw new WeatherException(e.getMessage());
        }
    }

    /**
     * 기상청 데이터 한국어로 변환
     * @param region
     * @param items
     * @return
     */
    public WeatherSummary summarizeWeather(String region, List<Map<String, Object>> items) {
        String temperature = null;
        String humidity = null;
        String precipitationType = null;
        String precipitationAmount = null;
        String windSpeed = null;
        String windDirection = null;

        for (Map<String, Object> item : items) {
            String category = (String) item.get("category");

            switch (category) {
                case "T1H": // 기온
                    temperature = (String) item.get("obsrValue");
                    break;
                case "REH": // 습도
                    humidity = (String) item.get("obsrValue");
                    break;
                case "PTY": // 강수 형태
                    precipitationType = (String) item.get("obsrValue");
                    break;
                case "RN1": // 강수량
                    precipitationAmount = (String) item.get("obsrValue");
                    break;
                case "WSD": // 풍속
                    windSpeed = (String) item.get("obsrValue");
                    break;
                case "VEC": // 풍향
                    windDirection = (String) item.get("obsrValue");
                    break;
            }
        }

        return new WeatherSummary(region, temperature, humidity, precipitationType,
                precipitationAmount, windSpeed, windDirection);
    }
}
