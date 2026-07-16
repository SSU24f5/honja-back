package com.honjaopseoyae.domain.course.controller;

import org.springframework.web.bind.annotation.*;

import com.honjaopseoyae.domain.course.dto.response.RouteResponseDto;
import com.honjaopseoyae.domain.course.service.RouteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}")
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/route")
    public RouteResponseDto getRoute(
            @PathVariable Long courseId,
            @RequestParam Long originPlaceId,
            @RequestParam Long destinationPlaceId,
            @RequestParam(defaultValue = "RECOMMEND") String priority
    ) {
        return routeService.getRoute(courseId, originPlaceId, destinationPlaceId, priority);
    }
}