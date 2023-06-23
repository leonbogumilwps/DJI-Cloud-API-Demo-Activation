package com.dji.sample.flightauthorization.ussp;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.dji.sample.flightauthorization.config.FlightOperationConfigurationProperties;
import com.dji.sample.flightauthorization.ussp.dto.request.LoginDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.SubmitFlightAuthorizationRequestDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.ApprovalStatusUASDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.AuthenticationInfoDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;

public class USSPFlightAuthorizationRepository {

	private String baseUrl;

	private RestTemplate restTemplate;

	private HttpHeaders headers;

	public USSPFlightAuthorizationRepository(String usspBaseUrl,
		FlightOperationConfigurationProperties configurationProperties) {
		this.baseUrl = usspBaseUrl;
		this.initRestTemplate(configurationProperties);
	}

	private void initRestTemplate(FlightOperationConfigurationProperties configurationProperties) {
		this.restTemplate = new RestTemplate();
		String authToken = this.login(configurationProperties);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authToken);
		this.headers = httpHeaders;
		restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws
				IOException {
				request.getHeaders().setBearerAuth(authToken);
				return execution.execute(request, body);
			}
		});
	}

	private String login(FlightOperationConfigurationProperties configurationProperties) {
		LoginDTO login = new LoginDTO(configurationProperties.getUsername(), configurationProperties.getPassword());
		ResponseEntity<AuthenticationInfoDTO> authenticationInfoDTO = restTemplate.postForEntity(
			baseUrl + "/web/auth/signin", login, AuthenticationInfoDTO.class);
		return authenticationInfoDTO.getBody().getJwtAuthToken();
	}

	public ResponseEntity<FlightOperationDetailDTO> findByFlightOperationId(String flightOperationId) {
		return restTemplate.getForEntity(
			baseUrl + "/web/flightapprovalrequests/" + flightOperationId,
			FlightOperationDetailDTO.class);
	}

	public ResponseEntity<ApprovalStatusUASDTO> findStatusByFlightOperationId(String flightOperationId) {
		return restTemplate.getForEntity(
			baseUrl + "/uas/flightapprovalrequest/" + flightOperationId + "/status",
			ApprovalStatusUASDTO.class);
	}

	public ResponseEntity<String> submitRequest(SubmitFlightAuthorizationRequestDTO requestDto) {
		return restTemplate.postForEntity(
			baseUrl + "/uas/flightapprovalrequest/submit",
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
