package com.nuttty.eureka.ai.application.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSummary {

    private String region;
    private String temperature; // 기온(T1H)
    private String humidity;    // 습도(REH)
    private String precipitationType; // 강수량(PTY)
    private String precipitationAmount; // 강수 형태(RN1)
    private String windSpeed;   // 풍속(WSD)
    private String windDirection; // 풍향(VEC)

    @Override
    public String toString() {
        return "지역: " + region + "\n" +
                "기온: " + temperature + "°C\n" +
                "습도: " + humidity + "%\n" +
                "강수 형태: " + precipitationType + "\n" +
                "강수량: " + precipitationAmount + "mm\n" +
                "풍속: " + windSpeed + "m/s\n" +
                "풍향: " + windDirection + "°";
    }
}
