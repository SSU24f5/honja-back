package com.honjaopseoyae.domain.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honjaopseoyae.domain.weather.converter.KmaGridConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class KmaWeatherClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KmaGridConverter kmaGridConverter;

    @Value("${kma.service-key}")
    private String serviceKey;

    @Value("${kma.base-url}")
    private String baseUrl;

    public record FcstItem(int skyCode, int ptyCode, double temperature) {}

    public FcstItem getUltraSrtFcst(double lat, double lon) {
        KmaGridConverter.Grid grid = kmaGridConverter.toGrid(lat, lon);
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime baseDateTime = now.getMinute() < 45 ? now.minusHours(1) : now;
        String baseDate = baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = baseDateTime.format(DateTimeFormatter.ofPattern("HH")) + "30";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/getUltraSrtFcst")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 60)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", grid.nx())
                .queryParam("ny", grid.ny())
                .build(true)
                .toUri();

        String rawResponse = restTemplate.getForObject(uri, String.class);
        return parse(rawResponse, now);
    }

    private FcstItem parse(String rawResponse, LocalDateTime now) {
        try {
            JsonNode items = objectMapper.readTree(rawResponse)
                    .path("response").path("body").path("items").path("item");

            String targetTime = now.format(DateTimeFormatter.ofPattern("HH")) + "00";

            int sky = 1, pty = 0;
            double temp = 0;
            for (JsonNode item : items) {
                if (!item.path("fcstTime").asText().equals(targetTime)) continue;
                String category = item.path("category").asText();
                switch (category) {
                    case "SKY" -> sky = item.path("fcstValue").asInt();
                    case "PTY" -> pty = item.path("fcstValue").asInt();
                    case "T1H" -> temp = item.path("fcstValue").asDouble();
                }
            }
            return new FcstItem(sky, pty, temp);
        } catch (Exception e) {
            throw new IllegalStateException("기상청 응답 파싱 실패", e);
        }
    }
}