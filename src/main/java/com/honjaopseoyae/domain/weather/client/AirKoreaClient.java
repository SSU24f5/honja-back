package com.honjaopseoyae.domain.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AirKoreaClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${airkorea.service-key}")
    private String serviceKey;

    @Value("${airkorea.base-url}")
    private String baseUrl;

    public record DustInfo(Integer pm10Value, Integer pm25Value, String grade) {}

    private static final String[] GRADE_LABEL = {"", "좋음", "보통", "나쁨", "매우나쁨"};

    public DustInfo getRealtimeDust(String stationName) {
        String uriString = UriComponentsBuilder.fromHttpUrl(baseUrl + "/getMsrstnAcctoRltmMesureDnsty")
                .queryParam("stationName", stationName)
                .queryParam("dataTerm", "DAILY")
                .queryParam("ver", "1.3")
                .queryParam("returnType", "json")
                .build()
                .encode()
                .toUriString();

        URI uri = URI.create(uriString + "&serviceKey=" + serviceKey);
        String rawResponse = restTemplate.getForObject(uri, String.class);

        return parse(rawResponse);
    }

    private DustInfo parse(String rawResponse) {
        try {
            JsonNode items = objectMapper.readTree(rawResponse)
                    .path("response").path("body").path("items");

            if (items.isMissingNode() || !items.isArray() || items.isEmpty()) {
                return new DustInfo(null, null, "데이터없음");
            }

            JsonNode validItem = null;
            for (JsonNode item : items) {
                String pm10Value = item.path("pm10Value").asText();
                String pm25Value = item.path("pm25Value").asText();

                if (!"통신장애".equals(item.path("pm10Flag").asText()) &&
                        !"통신장애".equals(item.path("pm25Flag").asText()) &&
                        !"-".equals(pm10Value) && !"-".equals(pm25Value)) {
                    validItem = item;
                    break;
                }
            }

            if (validItem == null) {
                validItem = items.get(0);
            }

            Integer pm10 = validItem.path("pm10Value").asText().matches("\\d+")
                    ? Integer.parseInt(validItem.path("pm10Value").asText()) : null;
            Integer pm25 = validItem.path("pm25Value").asText().matches("\\d+")
                    ? Integer.parseInt(validItem.path("pm25Value").asText()) : null;

            int gradeCode = validItem.path("pm10Grade").asText().matches("\\d+")
                    ? Integer.parseInt(validItem.path("pm10Grade").asText()) : 2;

            String grade = (gradeCode >= 0 && gradeCode < GRADE_LABEL.length)
                    ? GRADE_LABEL[gradeCode] : "보통";

            return new DustInfo(pm10, pm25, grade);
        } catch (Exception e) {
            throw new IllegalStateException("에어코리아 응답 파싱 실패", e);
        }
    }
}