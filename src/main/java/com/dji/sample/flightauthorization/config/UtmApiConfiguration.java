package com.dji.sample.flightauthorization.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import com.dji.sample.configuration.SpringBeanConfiguration;

import de.hhlasky.uassimulator.api.ussp.sender.ApiClient;

@Configuration
public class UtmApiConfiguration {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private SpringBeanConfiguration jacksonConfiguration;

	@Value("${apiclient.basepath}")
	private String basepath;

	@Value("${apiclient.username}")
	private String apiUsername;

	@Value("${apiclient.password}")
	private String apiPassword;

	@Bean
	public ApiClient apiClient() {
		WebClient webClient = uasApiWebClient();
		ApiClient apiClient = new ApiClient(webClient);
		String version = extractVersion(apiClient.getBasePath());
		apiClient.setBasePath(basepath + version);

		return apiClient;
	}

	private String extractVersion(String basePath) {
		String[] parts = basePath.split("/");
		return parts[parts.length - 1];
	}

	private WebClient uasApiWebClient() {
		OAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
		OAuth2AuthorizedClientProvider authorizedClientProvider =
			OAuth2AuthorizedClientProviderBuilder.builder()
				.password()
				.refreshToken()
				.build();
		AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
			new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		authorizedClientManager.setContextAttributesMapper(authorizeRequest -> {
			Map<String, Object> contextAttributes = new HashMap<>();
			contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, apiUsername);
			contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, apiPassword);
			return contextAttributes;
		});

		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		oauth2Client.setDefaultClientRegistrationId("uas-api");

		return WebClient.builder()
			.apply(oauth2Client.oauth2Configuration())
			.codecs(clientDefaultCodecsConfigurer -> {
				clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(jacksonConfiguration.objectMapper(new Jackson2ObjectMapperBuilder()), MediaType.APPLICATION_JSON));
				clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(jacksonConfiguration.objectMapper(new Jackson2ObjectMapperBuilder()), MediaType.APPLICATION_JSON));
			})
			.build();
	}
}
