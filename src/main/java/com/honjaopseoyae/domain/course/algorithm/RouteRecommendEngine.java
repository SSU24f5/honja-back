package com.honjaopseoyae.domain.course.algorithm;

import com.honjaopseoyae.domain.course.entity.enums.OrderType;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteRecommendEngine {

	public List<CoursePlace> recommend(List<CoursePlace> coursePlaces) {

		CoursePlace start = findByOrderType(coursePlaces, OrderType.START);
		CoursePlace end = findByOrderType(coursePlaces, OrderType.END);

		List<CoursePlace> waypoints = coursePlaces.stream()
			.filter(cp -> cp.getOrderType() == OrderType.WAYPOINT)
			.toList();

		if (waypoints.isEmpty()) {
			return List.of(start, end);
		}

		double[][] distanceMatrix = createDistanceMatrix(waypoints);
		SearchState state = new SearchState(waypoints, end, distanceMatrix);

		boolean[] visited = new boolean[waypoints.size()];
		List<CoursePlace> route = new ArrayList<>();

		dfs(start, -1, visited, route, 0.0, state);

		List<CoursePlace> result = new ArrayList<>();
		result.add(start);
		result.addAll(state.bestRoute);
		result.add(end);
		return result;
	}

	private CoursePlace findByOrderType(List<CoursePlace> places, OrderType type) {
		return places.stream()
			.filter(cp -> cp.getOrderType() == type)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("코스에 " + type + " 타입 장소가 없습니다."));
	}

	private void dfs(CoursePlace current, int currentIndex, boolean[] visited,
		List<CoursePlace> route, double score, SearchState state) {

		if (score >= state.bestScore) {
			return; // 가지치기
		}

		if (route.size() == state.waypoints.size()) {
			double finalScore = score + DistanceCalculator.calculate(
				current.getPlace().getMapy(), current.getPlace().getMapx(),
				state.end.getPlace().getMapy(), state.end.getPlace().getMapx());

			if (finalScore < state.bestScore) {
				state.bestScore = finalScore;
				state.bestRoute = new ArrayList<>(route);
			}
			return;
		}

		for (int i = 0; i < state.waypoints.size(); i++) {
			if (visited[i]) {
				continue;
			}

			CoursePlace next = state.waypoints.get(i);

			// start -> 첫 waypoint 구간은 매트릭스에 없으므로 직접 계산
			double distance = (currentIndex == -1)
				? DistanceCalculator.calculate(
				current.getPlace().getMapy(), current.getPlace().getMapx(),
				next.getPlace().getMapy(), next.getPlace().getMapx())
				: state.distanceMatrix[currentIndex][i];

			double penalty = calculatePenalty(current, next, route);

			visited[i] = true;
			route.add(next);

			dfs(next, i, visited, route, score + distance + penalty, state);

			route.remove(route.size() - 1);
			visited[i] = false;
		}
	}

	private double[][] createDistanceMatrix(List<CoursePlace> waypoints) {
		int n = waypoints.size();
		double[][] matrix = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				double distance = DistanceCalculator.calculate(
					waypoints.get(i).getPlace().getMapy(),waypoints.get(i).getPlace().getMapx(),
					waypoints.get(j).getPlace().getMapy(),waypoints.get(j).getPlace().getMapx());
					matrix[i][j] = distance;
				matrix[j][i] = distance;
			}
		}
		return matrix;
	}

	private double calculatePenalty(CoursePlace current, CoursePlace next, List<CoursePlace> route) {
		PlaceCategory currentCategory = PlaceCategory.from(current.getPlace());
		PlaceCategory nextCategory = PlaceCategory.from(next.getPlace());

		double penalty = 0;

		if (currentCategory == nextCategory) {
			penalty += switch (currentCategory) {
				case RESTAURANT -> 1200;
				case CAFE -> 800;
				case TOUR -> 300;
				case SHOPPING -> 500;
				default -> 200;
			};
		}

		if (route.size() >= 2) {
			PlaceCategory prev1 = PlaceCategory.from(route.get(route.size() - 1).getPlace());
			PlaceCategory prev2 = PlaceCategory.from(route.get(route.size() - 2).getPlace());

			if (prev1 == prev2 && prev1 == nextCategory) {
				penalty += 1500;
			}
		}

		return penalty;
	}

	/** DFS 탐색 동안만 살아있는 상태 객체. 필드로 두지 않고 매번 새로 생성해서 스레드 안전성을 확보. */
	private static class SearchState {
		final List<CoursePlace> waypoints;
		final CoursePlace end;
		final double[][] distanceMatrix;
		double bestScore = Double.MAX_VALUE;
		List<CoursePlace> bestRoute = new ArrayList<>();

		SearchState(List<CoursePlace> waypoints, CoursePlace end, double[][] distanceMatrix) {
			this.waypoints = waypoints;
			this.end = end;
			this.distanceMatrix = distanceMatrix;
		}
	}
}