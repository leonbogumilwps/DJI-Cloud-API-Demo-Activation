package com.dji.sample.flightauthorization.ussp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.dji.sample.flightauthorization.ussp.dto.request.SubmitFlightAuthorizationRequestDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;

public class USSPFlightAuthorizationRepository {

	private String baseUrl;
	private RestTemplate restTemplate;

	public USSPFlightAuthorizationRepository(String usspBaseUrl, RestTemplate restTemplate) {
		this.baseUrl = usspBaseUrl;
		this.restTemplate = restTemplate;
	}

	public ResponseEntity<FlightOperationDetailDTO> findByFlightOperationId(String flightOperationId) {
		return restTemplate.getForEntity(
			baseUrl + "/authorisation-requests/" + flightOperationId,
			FlightOperationDetailDTO.class);
	}

	public ResponseEntity<String> submitRequest(SubmitFlightAuthorizationRequestDTO requestDto) {
		return restTemplate.postForEntity(
			baseUrl + "/authorisation-requests",
			requestDto,
			String.class);
	}

	public ResponseEntity cancelByFlightOperationId(String flightOperationId) {
		return restTemplate.exchange(
			baseUrl + "/authorisation-requests/" + flightOperationId + "/cancellations",
			HttpMethod.PUT,
			null,
			Void.class);
	}
}
