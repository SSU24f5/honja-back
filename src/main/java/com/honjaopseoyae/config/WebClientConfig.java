package com.honjaopseoyae.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

	@Bean
	@Primary
	public WebClient tourApiWebClient() {
		String baseUrl = "https://apis.data.go.kr/B551011";
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

		return WebClient.builder()
			.uriBuilderFactory(factory)
			.baseUrl(baseUrl)
			.build();
	}

	@Bean
	public WebClient kakaoMobilityWebClient(
		@Value("${kakao.base-url}") String baseUrl,
		@Value("${kakao.rest-api-key}") String restApiKey
	) {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
			.build();
	}

	@Bean
	@Qualifier("kakaoLocalWebClient")
	public WebClient kakaoLocalWebClient(
		@Value("${kakao.local.base-url}") String baseUrl,
		@Value("${kakao.rest-api-key}") String apiKey
	) {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
			.build();
	}
}
