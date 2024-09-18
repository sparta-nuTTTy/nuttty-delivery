package com.nuttty.eureka.order.application.fegin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

// Directions API 응답 DTO
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse implements Serializable {
    private int code;
    private String message;
    private String currentDateTime;
    private Route route;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route implements Serializable {
        @JsonProperty("traoptimal") // 추가된 필드
        private List<Trafast> traoptimal; // List<Trafast>로 정의

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Trafast implements Serializable {
            @JsonProperty("summary")
            private Summary summary;

            @JsonProperty("path")
            private List<List<Double>> path; // 경로 정보

            @JsonProperty("section")
            private List<Section> section; // 구간 정보

            @JsonProperty("guide")
            private List<Guide> guide; // 안내 정보

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Summary implements Serializable {
                @JsonProperty("start")
                private Location start;

                @JsonProperty("goal")
                private Location goal;

                @JsonProperty("distance")
                private Double distance;

                @JsonProperty("duration")
                private Double duration;

                @JsonProperty("departureTime")
                private String departureTime;

                @JsonProperty("bbox")
                private List<List<Double>> bbox; // Bounding box 정보

                @JsonProperty("tollFare")
                private int tollFare; // 통행료

                @JsonProperty("taxiFare")
                private int taxiFare; // 택시 요금

                @JsonProperty("fuelPrice")
                private int fuelPrice; // 연료 가격

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Location {
                    private List<Double> location; // 경도, 위도
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Section implements Serializable {
                private int pointIndex;
                private int pointCount;
                private int distance;
                private String name;
                private int congestion;
                private int speed;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Guide implements Serializable {
                private int pointIndex;
                private int type;
                private String instructions;
                private int distance;
                private long duration;
            }
        }
    }
}
