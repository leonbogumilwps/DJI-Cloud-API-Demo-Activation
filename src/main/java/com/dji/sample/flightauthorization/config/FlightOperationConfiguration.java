package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.dji.sample.flightauthorization.api.FlightOperationRequestGuard;
import com.dji.sample.flightauthorization.api.ussp.sender.ActivationRequestProxy;
import com.dji.sample.flightauthorization.api.ussp.sender.AuthorizationProxy;
import com.dji.sample.flightauthorization.applicationservice.FlightOperationApplicationService;
import com.dji.sample.flightauthorization.domain.service.FlightOperationService;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;
import com.dji.sample.wayline.domain.service.WaylineService;
import com.dji.sample.wayline.service.IWaylineFileService;

@Configuration
@EnableJpaRepositories(basePackages = "com.dji.sample.flightauthorization.repository")
@EntityScan(basePackages = "com.dji.sample.flightauthorization.domain.entity")
@ConfigurationPropertiesScan
public class FlightOperationConfiguration {

	@Autowired
	private FlightOperationConfigurationProperties flightOperationConfigurationProperties;

	@Autowired
	private IWaylineFileService waylineFileService;

	@Autowired
	private FlightOperationRepository flightOperationRepository;

	@Autowired
	private AuthorizationProxy authorizationProxy;

	@Autowired
	private ActivationRequestProxy activationProxy;

	@Bean
	public FlightOperationRequestGuard flightAuthorizationRequestGuard() {
		return new FlightOperationRequestGuard();
	}

	@Bean
	public WaylineService waylineService() {
		return new WaylineService(waylineFileService);
	}

	@Bean
	public FlightOperationApplicationService flightAuthorizationApplicationService() {
		return new FlightOperationApplicationService(waylineService(), flightAuthorizationService(), authorizationProxy, activationProxy);
	}

	@Bean
	public FlightOperationService flightAuthorizationService() {
		return new FlightOperationService(flightOperationRepository);
	}
}
