package com.nuttty.eureka.ai.domain.service;

import com.nuttty.eureka.ai.config.JsonFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {

    private final RestTemplate restTemplate;

    /**
     * 기상청 API 호출
     */
    public String wheather() throws URISyntaxException {

        URI uri = new URI("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
                + "?serviceKey=nVQctOrihiA8%2BchYC1W803syMiJOHkuS%2BUfRixt1K7bHkyWTeXZI%2BGe80OfJCieIoPCeVtz9P0BCM6BISqskHw%3D%3D"
                + "&pageNo=1"
                + "&numOfRows=1000"
                + "&dataType=JSON"
                + "&base_date=20240915"
                + "&base_time=0600"
                + "&nx=61"
                + "&ny=126");

        // GET 요청 보내기
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        // 응답 출력
        System.out.println("Response code: " + response.getStatusCode());

        // JSON 응답을 포맷팅해서 출력
        String formattedJson = JsonFormatter.formatJson(response.getBody());
        System.out.println(formattedJson);

        return formattedJson;
    }
}
