package com.honjaopseoyae.course.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.util.ReflectionTestUtils;

import com.honjaopseoyae.domain.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseUpdateResponseDto;
import com.honjaopseoyae.domain.course.repository.CoursePlaceRepository;
import com.honjaopseoyae.domain.course.repository.CourseRepository;
import com.honjaopseoyae.domain.course.entity.CourseType;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.mapping.CoursePlace;
import com.honjaopseoyae.domain.course.service.CourseService;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;

@SpringBootTest
@Transactional
class CourseServiceTest {

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private CoursePlaceRepository coursePlaceRepository;

	private User testUser;
	private Course testCourse;
	private Place place1;
	private Place place2;

	@BeforeEach
	void setUp() {
		// 테스트용 유저 생성 및 저장
		testUser = User.builder()
				.nickname("테스트유저")
				.email("test@example.com")
				.petMode(false)
				.seniorMode(false)
				.build();
		userRepository.save(testUser);

		// 테스트용 코스 생성 및 저장
		testCourse = Course.builder()
				.user(testUser)
				.name("테스트 코스")
				.description("테스트 설명")
				.isPublic(true)
				.isExternal(true)
				.startDate(LocalDate.now())
				.endDate(LocalDate.now().plusDays(1))
				.courseType(CourseType.GENERAL)
				.build();
		courseRepository.save(testCourse);

		// 서울시청 부근 장소 1
		place1 = Place.builder()
				.mapx(126.9784)
				.mapy(37.5665)
				.contentId("11111")
				.petPlace(false)
				.barrierFree(false)
				.build();
		placeRepository.save(place1);

		// 서울역 부근 장소 2
		place2 = Place.builder()
				.mapx(126.9726)
				.mapy(37.5547)
				.contentId("22222")
				.petPlace(false)
				.barrierFree(false)
				.build();
		placeRepository.save(place2);
	}

	@Test
	void updateCourseTest() {
		// Given
		// 기존에 장소 1이 등록되어 있던 상태
		CoursePlace existingCoursePlace = CoursePlace.builder()
				.course(testCourse)
				.place(place1)
				.sortOrder(1L)
				.build();
		coursePlaceRepository.save(existingCoursePlace);

		// DTO 설정
		CourseUpdateRequestDto requestDto = new CourseUpdateRequestDto();
		ReflectionTestUtils.setField(requestDto, "courseId", testCourse.getId());

		// places 목록 설정 (기존 장소 1의 순서 유지 + 신규 장소 2 추가)
		List<CourseUpdateRequestDto.CoursePlaceItem> places = new ArrayList<>();

		// 1. 기존 장소 1 (수정)
		CourseUpdateRequestDto.CoursePlaceItem item1 = new CourseUpdateRequestDto.CoursePlaceItem();
		ReflectionTestUtils.setField(item1, "coursePlaceId", existingCoursePlace.getId());
		ReflectionTestUtils.setField(item1, "order", 1);
		places.add(item1);

		// 2. 신규 장소 2 (추가)
		CourseUpdateRequestDto.CoursePlaceItem item2 = new CourseUpdateRequestDto.CoursePlaceItem();
		ReflectionTestUtils.setField(item2, "placeId", place2.getId());
		ReflectionTestUtils.setField(item2, "order", 2);
		places.add(item2);

		CourseUpdateRequestDto.CourseDateItem dateItem = new CourseUpdateRequestDto.CourseDateItem();
		ReflectionTestUtils.setField(dateItem, "date", testCourse.getStartDate());
		ReflectionTestUtils.setField(dateItem, "places", places);
		ReflectionTestUtils.setField(requestDto, "dates", List.of(dateItem));

		// When
		CourseUpdateResponseDto response = courseService.updateCourse(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getCourseId()).isEqualTo(testCourse.getId());
		assertThat(response.getDates()).hasSize(1);
		assertThat(response.getDates().get(0).getPlaces()).hasSize(2);

		// 저장된 CoursePlace들 조회해서 검증
		List<CoursePlace> updatedCoursePlaces = coursePlaceRepository.findAllByCourseId(testCourse.getId());
		assertThat(updatedCoursePlaces).hasSize(2);

		// 정렬
		updatedCoursePlaces.sort((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

		CoursePlace cp1 = updatedCoursePlaces.get(0);
		CoursePlace cp2 = updatedCoursePlaces.get(1);

		// 첫 번째 장소는 시작점이므로 거리와 시간이 null이어야 함
		assertThat(cp1.getDistance()).isNull();
		assertThat(cp1.getTimeTaken()).isNull();

		// 두 번째 장소는 첫 번째 장소로부터의 거리 및 시간이 카카오모빌리티 API를 통해 채워져야 함
		assertThat(cp2.getDistance()).isNotNull();
		assertThat(cp2.getTimeTaken()).isNotNull();

		System.out.println("====== Course Update Test Success ======");
		System.out.println("Place 1 -> Place 2 Distance: " + cp2.getDistance() + " km");
		System.out.println("Place 1 -> Place 2 Time Taken: " + cp2.getTimeTaken() + " mins");
		System.out.println("=======================================");
	}

	@Test
	void getCourseDetailTest() {
		// Given
		// CoursePlace들 등록
		CoursePlace cp1 = CoursePlace.builder()
				.course(testCourse)
				.place(place1)
				.sortOrder(1L)
				.distance(null)
				.timeTaken(null)
				.build();
		coursePlaceRepository.save(cp1);

		CoursePlace cp2 = CoursePlace.builder()
				.course(testCourse)
				.place(place2)
				.sortOrder(2L)
				.distance("1.5")
				.timeTaken("10")
				.build();
		coursePlaceRepository.save(cp2);

		// When
		CourseDetailResponseDto detail = courseService.getCourseDetail(testCourse.getId());

		//Then
	}
}
