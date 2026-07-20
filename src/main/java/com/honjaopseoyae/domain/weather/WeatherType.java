package com.honjaopseoyae.domain.weather;

public enum WeatherType {
    SUNNY,          // 맑음
    PARTLY_CLOUDY,  // 구름많음
    CLOUDY,         // 흐림
    RAIN,           // 비
    RAIN_SNOW,      // 비/눈
    SNOW,           // 눈
    SHOWER;         // 소나기

    public static WeatherType of(int skyCode, int ptyCode) {
        if (ptyCode != 0) {
            return switch (ptyCode) {
                case 1, 5 -> RAIN;
                case 2, 6 -> RAIN_SNOW;
                case 3, 7 -> SNOW;
                case 4 -> SHOWER;
                default -> CLOUDY;
            };
        }
        return switch (skyCode) {
            case 1 -> SUNNY;
            case 3 -> PARTLY_CLOUDY;
            case 4 -> CLOUDY;
            default -> CLOUDY;
        };
    }

    public boolean isOutdoorFriendly() {
        return this == SUNNY || this == PARTLY_CLOUDY;
    }
}