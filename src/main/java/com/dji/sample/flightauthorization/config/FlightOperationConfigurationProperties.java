package com.dji.sample.flightauthorization.config;

import static com.dji.sample.common.util.Validators.isValidURL;
import static org.valid4j.Assertive.require;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties("ussp")
public class FlightOperationConfigurationProperties {

	private final String url;

	private final boolean mockDevices;

	public FlightOperationConfigurationProperties(String url, boolean mockDevices) {
		require(isValidURL(url), "url must be valid");
		this.url = url;
		this.mockDevices = mockDevices;
	}
}
