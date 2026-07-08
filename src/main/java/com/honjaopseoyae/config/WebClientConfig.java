package com.honjaopseoyae.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient tourApiWebClient() {
		String baseUrl = "https://apis.data.go.kr/B551011";
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

		return WebClient.builder()
			.uriBuilderFactory(factory)
			.baseUrl(baseUrl)
			.build();
	}
}