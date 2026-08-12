package com.honjaopseoyae.domain.course.controller;

import com.honjaopseoyae.domain.course.dto.request.CourseCreateRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseInviteRequestDto;
import com.honjaopseoyae.domain.course.dto.request.CourseUpdateRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseCreateResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseDetailResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationExistenceResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseListResponseDto;
import com.honjaopseoyae.domain.course.dto.response.CourseUpdateResponseDto;
import com.honjaopseoyae.domain.course.service.CourseService;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.apipayload.ApiResponse;
import com.honjaopseoyae.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "여행 코스 관리 API")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(
            summary = "내 코스 목록 조회 API",
            description = """
                    로그인한 사용자가 참여 중인 코스 목록을 조회합니다.

                    **Response:**
                    - 성공 시 200(OK)과 코스 목록을 반환합니다.
                    - 각 코스에는 코스 ID, 이름, 설명, 공개 여부, 여행 기간, 코스 유형, 참여자 프로필 이미지가 포함됩니다.
                    """)
    public ApiResponse<List<CourseListResponseDto>> getMyCourses(@AuthenticationPrincipal PrincipalDetails pd) {
        List<CourseListResponseDto> response = courseService.getMyCourses(pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{courseId}")
    @Operation(
            summary = "코스 상세 조회 API",
            description = """
                    코스 ID로 코스의 상세 정보와 날짜별 장소 목록을 조회합니다.

                    **Path Variable:**
                    - `courseId` (Long, required): 조회할 코스 ID

                    **Response:**
                    - 성공 시 200(OK)과 코스 기본 정보 및 날짜별 방문 장소, 장소 순서, 이동 거리와 예상 소요 시간을 반환합니다.
                    """)
    public ApiResponse<CourseDetailResponseDto> getCourseDetail(@PathVariable Long courseId, @AuthenticationPrincipal PrincipalDetails pd) {
        CourseDetailResponseDto response = courseService.getCourseDetail(courseId, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @PostMapping
    @Operation(
            summary = "코스 생성 API",
            description = """
                    로그인한 사용자를 코스 멤버로 포함하여 새 여행 코스를 생성합니다.

                    **Request Body:**
                    - `name` (String, required): 코스 이름
                    - `description` (String): 코스 설명
                    - `isPublic` (Boolean, required): 공개 여부
                    - `startDate` (LocalDate, required): 여행 시작일
                    - `endDate` (LocalDate, required): 여행 종료일
                    - `courseType` (CourseType, required): 코스 유형
                    - `tripCategory` (TripCategory, required): 여행 카테고리

                    **Response:**
                    - 성공 시 200(OK)과 생성된 코스 정보를 반환합니다.
                    """)
    public ApiResponse<CourseCreateResponseDto> createCourse(@RequestBody @Valid CourseCreateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails pd) {

        CourseCreateResponseDto response = courseService.createCourse(requestDto, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @PutMapping
    @Operation(
            summary = "코스 일정 및 장소 수정 API",
            description = """
                    코스의 날짜별 방문 장소와 방문 순서를 수정합니다. `coursePlaceId`가 없으면 새 장소로 추가하고, 있으면 기존 장소를 수정합니다.

                    **Request Body:**
                    - `courseId` (Long, required): 수정할 코스 ID
                    - `dates` (JSON Array, required): 날짜별 장소 목록
                    - `dates[].date` (LocalDate, required): 일정 날짜
                    - `dates[].places` (JSON Array, required): 해당 날짜의 장소 목록
                    - `dates[].places[].coursePlaceId` (Long): 기존 코스 장소 ID
                    - `dates[].places[].placeId` (Long): 추가할 장소 ID
                    - `dates[].places[].order` (Integer, required): 방문 순서
                    - `dates[].places[].orderType` (OrderType): 이동 수단 또는 순서 유형
                    - `dates[].places[].placeType` (PlaceType, required): 장소 유형

                    **Response:**
                    - 성공 시 200(OK)과 수정된 날짜별 장소 정보를 반환합니다.
                    """)
    public ApiResponse<CourseUpdateResponseDto> updateCourse(@RequestBody @Valid CourseUpdateRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails pd) {
        CourseUpdateResponseDto response = courseService.updateCourse(requestDto, pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{courseId}")
    @Operation(
            summary = "코스 삭제 API",
            description = """
                    코스 ID에 해당하는 코스를 삭제합니다.

                    **Path Variable:**
                    - `courseId` (Long, required): 삭제할 코스 ID

                    **Response:**
                    - 성공 시 200(OK)과 삭제 완료 메시지를 반환합니다.
                    """)
    public ApiResponse<String> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.deleteCourse(courseId, pd.getUserId());
        return ApiResponse.onSuccess("코스가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/invitations")
    @Operation(
            summary = "코스 멤버 초대 API",
            description = """
                    이메일로 사용자를 코스에 초대합니다.

                    **Request Body:**
                    - `courseId` (Long, required): 초대할 코스 ID
                    - `email` (String, required): 초대할 사용자의 이메일

                    **Response:**
                    - 성공 시 200(OK)과 초대 요청 완료 메시지를 반환합니다.
                    """)
    public ApiResponse<String> inviteMember(
            @RequestBody @Valid CourseInviteRequestDto requestDto,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.inviteMemberByEmail(requestDto, pd.getUserId());
        return ApiResponse.onSuccess("초대 요청이 성공적으로 발송되었습니다.");
    }

    @GetMapping("/invitations")
    @Operation(
            summary = "내 코스 초대 목록 조회 API",
            description = """
                    로그인한 사용자가 받은 코스 초대 목록을 조회합니다.

                    **Response:**
                    - 성공 시 200(OK)과 초대 ID, 코스 정보, 초대한 사용자 정보, 초대 상태를 반환합니다.
                    """)
    public ApiResponse<List<CourseInvitationResponseDto>> getMyInvitations(
            @AuthenticationPrincipal PrincipalDetails pd) {
        List<CourseInvitationResponseDto> response = courseService.getMyInvitations(pd.getUserId());
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/invitations/exists")
    @Operation(summary = "대기 중인 코스 초대 존재 여부 조회 API")
    public ApiResponse<CourseInvitationExistenceResponseDto> hasPendingInvitations(
            @AuthenticationPrincipal PrincipalDetails pd) {
        boolean hasPendingInvitations = courseService.hasPendingInvitations(pd.getUserId());
        return ApiResponse.onSuccess(new CourseInvitationExistenceResponseDto(hasPendingInvitations));
    }

    @PostMapping("/invitations/{courseMemberId}/accept")
    @Operation(
            summary = "코스 초대 수락 API",
            description = """
                    받은 코스 초대를 수락하고 코스 멤버로 참여합니다.

                    **Path Variable:**
                    - `courseMemberId` (Long, required): 수락할 초대 ID

                    **Response:**
                    - 성공 시 200(OK)과 초대 수락 완료 메시지를 반환합니다.
                    """)
    public ApiResponse<String> acceptInvitation(
            @PathVariable Long courseMemberId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.acceptInvitation(courseMemberId, pd.getUserId());
        return ApiResponse.onSuccess("초대를 성공적으로 수락했습니다.");
    }

    @PostMapping("/invitations/{courseMemberId}/reject")
    @Operation(
            summary = "코스 초대 거절 API",
            description = """
                    받은 코스 초대를 거절합니다.

                    **Path Variable:**
                    - `courseMemberId` (Long, required): 거절할 초대 ID

                    **Response:**
                    - 성공 시 200(OK)과 초대 거절 완료 메시지를 반환합니다.
                    """)
    public ApiResponse<String> rejectInvitation(
            @PathVariable Long courseMemberId,
            @AuthenticationPrincipal PrincipalDetails pd) {
        courseService.rejectInvitation(courseMemberId, pd.getUserId());
        return ApiResponse.onSuccess("초대를 거절했습니다.");
    }
}
