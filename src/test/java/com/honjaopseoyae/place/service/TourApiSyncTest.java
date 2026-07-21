package com.honjaopseoyae.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.honjaopseoyae.domain.place.client.TourApiClient;
import com.honjaopseoyae.domain.place.dto.common.TourApiCommonResponse;
import com.honjaopseoyae.domain.place.dto.response.TourPlaceDto;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.domain.place.repository.PlaceRepository;
import com.honjaopseoyae.domain.place.service.TourApiServiceImpl;

@ExtendWith(MockitoExtension.class)
class TourApiSyncTest {

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private TourApiClient tourApiClient;

	@InjectMocks
	private TourApiServiceImpl tourApiService;

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
		apiDto.setCat3("A05020900");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(tourApiClient.getPlaces(anyString(), any()))
			.thenReturn(mockResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse);

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
		assertThat(savedPlace.getCat3()).isEqualTo("A05020900");
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
		apiDto.setCat3("A05020900");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(tourApiClient.getPlaces(anyString(), any()))
			.thenReturn(mockResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse);

		Place existingPlace = Place.builder()
				.contentId("12345")
				.contentType(12)
				.mapx(126.9784)
				.mapy(37.5665)
				.image("http://example.com/old-image.jpg")
				.barrierFree(true)
				.petPlace(true)
				.cat3("A05020100")
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
		assertThat(savedPlace.getCat3()).isEqualTo("A05020900");
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
		apiDto.setCat3("A05020900");

		TourApiCommonResponse<List<TourPlaceDto>> mockResponse = createMockResponse(List.of(apiDto));

		TourApiCommonResponse<List<TourPlaceDto>> emptyResponse = new TourApiCommonResponse<>();
		when(tourApiClient.getPlaces(anyString(), any()))
			.thenReturn(mockResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse, emptyResponse);

		Place existingPlace = Place.builder()
				.contentId("12345")
				.contentType(12)
				.mapx(126.9784)
				.mapy(37.5665)
				.image("http://example.com/image.jpg")
				.barrierFree(true)
				.petPlace(false)
				.cat3("A05020900")
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
