package com.dji.sample.flightauthorization.model.config;

import static com.dji.sample.common.util.Validators.isValidURL;
import static org.valid4j.Assertive.require;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties("ussp")
public class FlightAuthorizationConfigurationProperties {

	private final String url;

	public FlightAuthorizationConfigurationProperties(String url) {
		require(isValidURL(url), "url must be valid");
		this.url = url;
	}
}
