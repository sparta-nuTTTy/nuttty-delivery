package com.nuttty.eureka.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonFormatter {

    public static String formatJson(String json) {
        try {
            // ObjectMapper를 사용해 JSON 포맷팅
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(json, Object.class);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter(); // Pretty Print 설정
            return writer.writeValueAsString(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return json; // 오류 발생 시 원본 JSON 반환
        }
    }
}
