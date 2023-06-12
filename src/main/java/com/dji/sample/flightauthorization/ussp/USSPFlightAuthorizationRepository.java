package com.dji.sample.flightauthorization.ussp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.dji.sample.flightauthorization.ussp.view.FlightAuthorizationRequestView;

public class USSPFlightAuthorizationRepository {

	private String baseUrl;
	private RestTemplate restTemplate;

	public USSPFlightAuthorizationRepository(String usspBaseUrl, RestTemplate restTemplate) {
		this.baseUrl = usspBaseUrl;
		this.restTemplate = restTemplate;
	}

	public ResponseEntity<FlightAuthorizationRequestView> findByFlightOperationId(String flightOperationId) throws
		ResponseStatusException {
		return restTemplate.getForEntity(
			baseUrl + "/authorisation-requests/" + flightOperationId,
			FlightAuthorizationRequestView.class);
	}
}
