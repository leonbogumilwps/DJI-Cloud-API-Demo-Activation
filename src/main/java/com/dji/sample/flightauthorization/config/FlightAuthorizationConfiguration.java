package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.hhlasky.uassimulator.api.ussp.sender.FlightAuthorisationsApi;

@Configuration
public class FlightAuthorizationConfiguration {

	@Autowired
	private UtmApiConfiguration apiConfig;

	@Bean
	public FlightAuthorisationsApi flightAuthorisationRequestApi() {
		return new FlightAuthorisationsApi(apiConfig.apiClient());
	}
}
