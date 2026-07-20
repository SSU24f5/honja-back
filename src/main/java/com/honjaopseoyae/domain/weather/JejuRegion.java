package com.honjaopseoyae.domain.weather;

import com.honjaopseoyae.domain.weather.converter.KmaGridConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JejuRegion {

    JEJU_SI("제주시내권역", 33.4996, 126.5312, "제주"),
    NOHYEONG_YEONDONG("노형/연동권역", 33.4835, 126.4877, "연동"),
    AEWOL("애월권역", 33.4614, 126.3308, "애월"),
    HANLIM_HANGYEONG("한림/한경권역", 33.4145, 126.2694, "한림"),
    GUJWA_JOCHEON("구좌/조천권역", 33.5427, 126.6980, "조천"),
    SEONGSAN("성산권역", 33.4581, 126.9142, "성산"),
    PYOSEON("표선권역", 33.3256, 126.8331, "표선"),
    NAMWON("남원권역", 33.2794, 126.7178, "남원"),
    SEOGWIPO_SI("서귀포시내권역", 33.2541, 126.5602, "서귀포"),
    JUNGMUN("중문권역", 33.2508, 126.4128, "중문"),
    ANDEOK_DAEJEONG("안덕/대정권역", 33.2286, 126.2661, "대정");

    private final String displayName;
    private final double latitude;
    private final double longitude;
    private final String airkoreaStationName;

    // lazy initialization용 필드
    private Integer nx;
    private Integer ny;

    /**
     * 격자 좌표 반환
     */
    public KmaGridConverter.Grid getGrid() {
        ensureGrid();
        return new KmaGridConverter.Grid(nx, ny);
    }

    public int getNx() {
        ensureGrid();
        return nx;
    }

    public int getNy() {
        ensureGrid();
        return ny;
    }

    /**
     * 격자 좌표 lazy 초기화
     */
    private synchronized void ensureGrid() {
        if (nx == null || ny == null) {
            KmaGridConverter converter = new KmaGridConverter();
            KmaGridConverter.Grid grid = converter.toGrid(latitude, longitude);
            this.nx = grid.nx();
            this.ny = grid.ny();
        }
    }

    /**
     * 이름 또는 displayName으로 권역 찾기
     */
    public static JejuRegion from(String name) {
        for (JejuRegion region : values()) {
            if (region.name().equalsIgnoreCase(name) ||
                    region.displayName.equals(name)) {
                return region;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 제주 권역입니다: " + name);
    }

    /**
     * 사용자 위경도에서 가장 가까운 권역 찾기
     */
    public static JejuRegion findNearest(double latitude, double longitude) {
        JejuRegion nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (JejuRegion region : values()) {
            double distance = calculateDistance(
                    latitude, longitude,
                    region.latitude, region.longitude
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearest = region;
            }
        }
        return nearest;
    }

    /**
     * 두 좌표 간 거리 계산
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
