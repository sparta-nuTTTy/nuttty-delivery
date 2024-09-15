package com.nuttty.eureka.ai.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuttty.eureka.ai.exception.exceptionsdefined.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
public class GeminiApiService {

    private final RestTemplate restTemplate;

    /**
     * Gemini API 호출
     * @param message
     * @return
     * @throws JsonProcessingException
     */
    @Transactional
    public String callGemini(String message) throws JsonProcessingException {
        // 요청 URL
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAAEe26JCvfcuuzyl3lQc0T-B-QwLtClkA";

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 생성
        Map<String, Object> content = new HashMap<>();
        content.put("text", "허브 별로 주문 정보 정리해주세요. 최대한 짧게 요약해주세요" + message);

        Map<String, Object> parts = new HashMap<>();
        parts.put("parts", new Object[]{content});

        Map<String, Object> body = new HashMap<>();
        body.put("contents", new Object[]{parts});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 요청 후 응답 받기
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        } catch (RestClientException e) {
            log.error("GeminiApiService exception callGemini", e);
            throw new ClientException(e.getMessage());
        }

        if (response != null) {
            JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
            return jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }

        return null;
    }
}
