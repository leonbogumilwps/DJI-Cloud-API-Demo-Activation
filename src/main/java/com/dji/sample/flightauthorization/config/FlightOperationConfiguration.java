package com.dji.sample.flightauthorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import com.dji.sample.flightauthorization.api.FlightOperationRequestGuard;
import com.dji.sample.flightauthorization.applicationservice.FlightOperationApplicationService;
import com.dji.sample.flightauthorization.domain.service.FlightOperationService;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.manage.service.IDeviceService;
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
	private IDeviceService deviceService;

	@Autowired
	private FlightOperationRepository flightOperationRepository;

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
		return new FlightOperationApplicationService(waylineService(), flightAuthorizationService(),
			usspFlightAuthorizationRepository(), deviceService, flightOperationConfigurationProperties);
	}

	@Bean
	public USSPFlightAuthorizationRepository usspFlightAuthorizationRepository() {
		return new USSPFlightAuthorizationRepository(flightOperationConfigurationProperties.getUrl(),
			flightOperationConfigurationProperties);
	}

	@Bean
	public FlightOperationService flightAuthorizationService() {
		return new FlightOperationService(flightOperationRepository);
	}
}
