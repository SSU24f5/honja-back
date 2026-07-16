package com.honjaopseoyae.domain.course.dto.response;

import java.util.ArrayList;
import java.util.List;

public record RouteResponseDto(
        Long originPlaceId,
        Long destinationPlaceId,
        int distance,           // m
        int duration,           // sec
        Bound bound,
        List<Point> path,
        List<Guide> guides
) {
    public record Point(double lat, double lng) {}

    public record Bound(double minLat, double minLng, double maxLat, double maxLng) {}

    public record Guide(double lat, double lng, String guidance, int distance, int duration) {}

    public static RouteResponseDto of(Long originPlaceId, Long destinationPlaceId,
                                      KakaoDirectionsResponseDto.Route route) {

        List<Point> path = extractPath(route.getSections());

        return new RouteResponseDto(
                originPlaceId,
                destinationPlaceId,
                route.getSummary().getDistance(),
                route.getSummary().getDuration(),
                calculateBound(path),
                path,
                extractGuides(route.getSections())
        );
    }


    private static List<Point> extractPath(List<KakaoDirectionsResponseDto.Section> sections) {
        List<Point> path = new ArrayList<>();
        if (sections == null) return path;

        for (var section : sections) {
            if (section.getRoads() == null) continue;
            for (var road : section.getRoads()) {
                List<Double> v = road.getVertexes();
                if (v == null) continue;
                for (int i = 0; i + 1 < v.size(); i += 2) {
                    Point p = new Point(v.get(i + 1), v.get(i));   // (lat, lng)
                    if (path.isEmpty() || !path.get(path.size() - 1).equals(p)) {
                        path.add(p);
                    }
                }
            }
        }
        return path;
    }

    private static List<Guide> extractGuides(List<KakaoDirectionsResponseDto.Section> sections) {
        List<Guide> guides = new ArrayList<>();
        if (sections == null) return guides;

        for (var section : sections) {
            if (section.getGuides() == null) continue;
            for (var g : section.getGuides()) {
                guides.add(new Guide(g.getY(), g.getX(), g.getGuidance(),
                        g.getDistance(), g.getDuration()));
            }
        }
        return guides;
    }

    private static Bound calculateBound(List<Point> path) {
        if (path.isEmpty()) return new Bound(0, 0, 0, 0);
        return new Bound(
                path.stream().mapToDouble(Point::lat).min().orElse(0),
                path.stream().mapToDouble(Point::lng).min().orElse(0),
                path.stream().mapToDouble(Point::lat).max().orElse(0),
                path.stream().mapToDouble(Point::lng).max().orElse(0)
        );
    }
}