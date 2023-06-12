package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import com.dji.sample.flightauthorization.api.FlightAuthorizationRequestGuard;
import com.dji.sample.flightauthorization.applicationservice.FlightAuthorizationApplicationService;
import com.dji.sample.flightauthorization.domain.service.FlightAuthorizationService;
import com.dji.sample.flightauthorization.repository.FlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.wayline.service.IWaylineFileService;

@Configuration
@EnableJpaRepositories
@EntityScan
public class FlightAuthorizationConfiguration {

	@Autowired
	private FlightAuthorizationConfigurationProperties flightAuthorizationConfigurationProperties;

	@Autowired
	private IWaylineFileService waylineFileService;

	@Autowired
	private FlightAuthorizationRepository flightAuthorizationRepository;

	@Bean
	public FlightAuthorizationRequestGuard flightAuthorizationRequestGuard() {
		return new FlightAuthorizationRequestGuard();
	}

	@Bean
	public FlightAuthorizationApplicationService flightAuthorizationApplicationService() {
		return new FlightAuthorizationApplicationService(waylineFileService, flightAuthorizationService(), usspFlightAuthorizationRepository());
	}

	@Bean
	public USSPFlightAuthorizationRepository usspFlightAuthorizationRepository() {
		RestTemplate restTemplate = new RestTemplate();
		//TODO: add authentication to ussp restTemplate
		return new USSPFlightAuthorizationRepository(flightAuthorizationConfigurationProperties.getUrl(), restTemplate);
	}

	@Bean
	public FlightAuthorizationService flightAuthorizationService() {
		return new FlightAuthorizationService(flightAuthorizationRepository);
	}
}
