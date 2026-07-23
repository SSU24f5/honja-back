package com.honjaopseoyae.domain.place.util;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IndoorOutdoorClassifier {

    private static final List<String> INDOOR_KEYWORDS = List.of(
            "박물관", "미술관", "전시관", "실내", "체험관", "쇼핑몰", "아쿠아리움"
    );

    private static final List<String> OUTDOOR_KEYWORDS = List.of(
            "해수욕장", "해변", "폭포", "휴양림", "오름", "주상절리", "곶", "숲", "수목원", "길", "공원"
    );

    public boolean isIndoor(String title) {
        if (title == null) return false; // 정보 없으면 기본 실외 취급

        boolean hasIndoorKeyword = INDOOR_KEYWORDS.stream().anyMatch(title::contains);
        boolean hasOutdoorKeyword = OUTDOOR_KEYWORDS.stream().anyMatch(title::contains);

        // 둘 다 걸리면 (예: "제주불빛정원") 실내 키워드 우선 (실내 시설 요소가 있으면 안전하게 실내로)
        if (hasIndoorKeyword) return true;
        return false; // 실외 키워드 걸리거나, 둘 다 안 걸리면 기본 실외
    }
}