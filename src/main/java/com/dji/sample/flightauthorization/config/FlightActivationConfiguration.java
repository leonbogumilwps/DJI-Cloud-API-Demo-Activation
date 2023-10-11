package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.hhlasky.uassimulator.api.ussp.sender.FlightActivationsApi;
import de.hhlasky.uassimulator.api.ussp.sender.FlightAuthorisationsApi;

@Configuration
public class FlightActivationConfiguration {

	@Autowired
	private UtmApiConfiguration apiConfig;

	@Bean
	public FlightActivationsApi flightAuthorisationActivationApi() {
		return new FlightActivationsApi(apiConfig.apiClient());
	}
}
