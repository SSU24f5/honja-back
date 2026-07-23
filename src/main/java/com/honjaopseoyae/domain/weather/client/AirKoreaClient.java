package com.honjaopseoyae.domain.weather.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.proj4j.*;
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

    @Value("${airkorea.station-base-url}")
    private String stationBaseUrl;


    public record DustInfo(Integer pm10Value, Integer pm25Value, String grade) {}

    private static final String[] GRADE_LABEL = {"", "좋음", "보통", "나쁨", "매우나쁨"};

    public String findNearestStation(double lat, double lon) {
        double[] tmCoord = convertToTm(lat, lon);

        String uriString = UriComponentsBuilder.fromHttpUrl(stationBaseUrl + "/getNearbyMsrstnList")
                .queryParam("tmX", tmCoord[0])
                .queryParam("tmY", tmCoord[1])
                .queryParam("returnType", "json")
                .build()
                .encode()
                .toUriString();

        URI uri = URI.create(uriString + "&serviceKey=" + serviceKey);
        String rawResponse = restTemplate.getForObject(uri, String.class);

        return parseNearestStationName(rawResponse);
    }

    private double[] convertToTm(double lat, double lon) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
        CoordinateReferenceSystem tmMid = crsFactory.createFromName("EPSG:2097");

        CoordinateTransform transform = new CoordinateTransformFactory()
                .createTransform(wgs84, tmMid);

        ProjCoordinate src = new ProjCoordinate(lon, lat);
        ProjCoordinate result = new ProjCoordinate();
        transform.transform(src, result);

        return new double[]{result.x, result.y};
    }

    private String parseNearestStationName(String rawResponse) {
        try {
            JsonNode items = objectMapper.readTree(rawResponse)
                    .path("response").path("body").path("items");

            if (items.isMissingNode() || !items.isArray() || items.isEmpty()) {
                throw new IllegalStateException("근접 측정소를 찾을 수 없습니다.");
            }
            // tm(거리)값 기준 가장 작은(가까운) 측정소 선택
            JsonNode nearest = null;
            double minTm = Double.MAX_VALUE;
            for (JsonNode item : items) {
                double tm = item.path("tm").asDouble(Double.MAX_VALUE);
                if (tm < minTm) {
                    minTm = tm;
                    nearest = item;
                }
            }
            return nearest.path("stationName").asText();
        } catch (Exception e) {
            throw new IllegalStateException("근접측정소 응답 파싱 실패", e);
        }
    }

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