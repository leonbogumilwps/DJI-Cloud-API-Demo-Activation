package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.hhlasky.uassimulator.api.ussp.sender.FlightTrackingApi;

@Configuration
public class FlightTrackingConfiguration {
	@Autowired
	private UtmApiConfiguration utmApiConfiguration;

	@Bean
	public FlightTrackingApi flightTrackingApi(){
		return new FlightTrackingApi(utmApiConfiguration.apiClient());
	}
}
