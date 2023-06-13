package com.dji.sample.flightauthorization.ussp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.dji.sample.flightauthorization.ussp.command.SubmitFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.ussp.view.FlightAuthorizationRequestView;

public class USSPFlightAuthorizationRepository {

	private String baseUrl;
	private RestTemplate restTemplate;

	public USSPFlightAuthorizationRepository(String usspBaseUrl, RestTemplate restTemplate) {
		this.baseUrl = usspBaseUrl;
		this.restTemplate = restTemplate;
	}

	public ResponseEntity<FlightAuthorizationRequestView> findByFlightOperationId(String flightOperationId) {
		return restTemplate.getForEntity(
			baseUrl + "/authorisation-requests/" + flightOperationId,
			FlightAuthorizationRequestView.class);
	}

	public ResponseEntity<String> submitRequest(SubmitFlightAuthorizationRequestCommand command) {
		return restTemplate.postForEntity(
			baseUrl + "/authorisation-requests",
			command,
			String.class);
	}

	public void cancelByFlightOperationId(String flightOperationId) {

	}
}
