package com.honjaopseoyae.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.honjaopseoyae.place.entity.Place;
import com.honjaopseoyae.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.place.repository.PlaceRepository;

@ExtendWith(MockitoExtension.class)
class TourApiSyncTest {

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private WebClient webClient;

	@InjectMocks
	private TourApiServiceImpl tourApiService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(tourApiService, "serviceKey", "test-service-key");
	}

	@SuppressWarnings("unchecked")
	@Test
	void syncTourPlacesWithApi_whenPlaceDoesNotExist_shouldInsertNewPlace() {
		// Given
		TourPlaceDto apiDto = new TourPlaceDto();
		apiDto.setContentid("12345");
		apiDto.setContenttypeid("12");
		apiDto.setMapx("126.9784");
		apiDto.setMapy("37.5665");
		apiDto.setFirstimage("http://example.com/image.jpg");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
			.thenReturn(
				Mono.just(mockResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse)
			);

		when(placeRepository.findAllByContentIdIn(anyList())).thenReturn(Collections.emptyList());

		// When
		tourApiService.syncTourPlacesWithApi();

		// Then
		ArgumentCaptor<List<Place>> listCaptor = ArgumentCaptor.forClass(List.class);
		verify(placeRepository).saveAll(listCaptor.capture());
		
		List<Place> savedPlaces = listCaptor.getValue();
		assertThat(savedPlaces).hasSize(1);
		
		Place savedPlace = savedPlaces.get(0);
		assertThat(savedPlace.getContentId()).isEqualTo("12345");
		assertThat(savedPlace.getContentType()).isEqualTo(12);
		assertThat(savedPlace.getMapx()).isEqualTo(126.9784);
		assertThat(savedPlace.getMapy()).isEqualTo(37.5665);
		assertThat(savedPlace.getImage()).isEqualTo("http://example.com/image.jpg");
		assertThat(savedPlace.isBarrierFree()).isTrue();
		assertThat(savedPlace.isPetPlace()).isFalse();
	}

	@SuppressWarnings("unchecked")
	@Test
	void syncTourPlacesWithApi_whenPlaceExistsButChanged_shouldUpdatePlaceAndPreservePetPlace() {
		// Given
		TourPlaceDto apiDto = new TourPlaceDto();
		apiDto.setContentid("12345");
		apiDto.setContenttypeid("12");
		apiDto.setMapx("126.9784");
		apiDto.setMapy("37.5665");
		apiDto.setFirstimage("http://example.com/new-image.jpg");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
			.thenReturn(
				Mono.just(mockResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse)
			);

		Place existingPlace = Place.builder()
				.contentId("12345")
				.contentType(12)
				.mapx(126.9784)
				.mapy(37.5665)
				.image("http://example.com/old-image.jpg")
				.barrierFree(true)
				.petPlace(true)
				.build();
		when(placeRepository.findAllByContentIdIn(anyList())).thenReturn(List.of(existingPlace));

		// When
		tourApiService.syncTourPlacesWithApi();

		// Then
		ArgumentCaptor<List<Place>> listCaptor = ArgumentCaptor.forClass(List.class);
		verify(placeRepository).saveAll(listCaptor.capture());
		
		List<Place> savedPlaces = listCaptor.getValue();
		assertThat(savedPlaces).hasSize(1);
		
		Place savedPlace = savedPlaces.get(0);
		assertThat(savedPlace.getContentId()).isEqualTo("12345");
		assertThat(savedPlace.getImage()).isEqualTo("http://example.com/new-image.jpg");
		assertThat(savedPlace.isBarrierFree()).isTrue();
		assertThat(savedPlace.isPetPlace()).isTrue();
	}

	@SuppressWarnings("unchecked")
	@Test
	void syncTourPlacesWithApi_whenPlaceExistsAndNotChanged_shouldNotSave() {
		// Given
		TourPlaceDto apiDto = new TourPlaceDto();
		apiDto.setContentid("12345");
		apiDto.setContenttypeid("12");
		apiDto.setMapx("126.9784");
		apiDto.setMapy("37.5665");
		apiDto.setFirstimage("http://example.com/image.jpg");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
		WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
			.thenReturn(
				Mono.just(mockResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse),
				Mono.just(emptyResponse)
			);

		Place existingPlace = Place.builder()
				.contentId("12345")
				.contentType(12)
				.mapx(126.9784)
				.mapy(37.5665)
				.image("http://example.com/image.jpg")
				.barrierFree(true)
				.petPlace(false)
				.build();
		when(placeRepository.findAllByContentIdIn(anyList())).thenReturn(List.of(existingPlace));

		// When
		tourApiService.syncTourPlacesWithApi();

		// Then
		verify(placeRepository, never()).saveAll(anyList());
	}

	private TourApiCommonResponse<List<TourPlaceDto>> createMockResponse(List<TourPlaceDto> itemsList) {
		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = new TourApiCommonResponse<>();
		TourApiCommonResponse.TourResponse<List<TourPlaceDto>> response = new TourApiCommonResponse.TourResponse<>();
		TourApiCommonResponse.Body<List<TourPlaceDto>> body = new TourApiCommonResponse.Body<>();
		TourApiCommonResponse.Items<List<TourPlaceDto>> items = new TourApiCommonResponse.Items<>();

		items.setItem(itemsList);
		body.setItems(items);
		body.setTotalCount(itemsList.size());
		response.setBody(body);
		mockResponse.setResponse(response);

		return mockResponse;
	}
}
