package com.nuttty.eureka.order.application.feign.dto;

import lombok.Getter;

@Getter
public class WayPoint {
    // 경도
    private Double longitude;
    // 위도
    private Double latitude;

    public WayPoint(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}
