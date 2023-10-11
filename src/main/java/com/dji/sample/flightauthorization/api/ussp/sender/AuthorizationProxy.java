package com.dji.sample.flightauthorization.api.ussp.sender;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestResponseDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationStatusDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationStatusEnumDto;
import de.hhlasky.uassimulator.api.ussp.sender.FlightAuthorisationsApi;

@Service
public class AuthorizationProxy {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationProxy.class);
	private static final Duration REST_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
	private static final Duration STATUS_CHANGE_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

	private final FlightAuthorisationsApi authorisationRequestApi;

	public AuthorizationProxy(FlightAuthorisationsApi authorisationRequestApi) {
		this.authorisationRequestApi = authorisationRequestApi;
	}

	public AuthorisationRequestResponseDto requestAuthorizationAndWait(AuthorisationRequestDto request) {
		return executeAndLogErrors(() -> {
			String flightOperationId = authorisationRequestApi.submitAuthorisationRequest(request)
				.block(REST_TIMEOUT);
			flightOperationId = flightOperationId.replace("\"", "");
			LOGGER.info("Flug {} eingetragen", flightOperationId);
			return waitForAuthorizationStatusUpdate(flightOperationId, Set.of(AuthorisationStatusEnumDto.APPROVED, AuthorisationStatusEnumDto.REJECTED));
		});
	}

	public AuthorisationRequestResponseDto getAuthorization(String flightOperationId) {
		return executeAndLogErrors(() -> {
			AuthorisationRequestResponseDto result =
				authorisationRequestApi.getSingleAuthorisationRequest(flightOperationId)
					.block(REST_TIMEOUT);
			LOGGER.info("Flug {} abgefragt", flightOperationId);
			return result;
		});
	}

	public AuthorisationRequestResponseDto cancelAuthorizationAndWait(String flightOperationId) {
		return executeAndLogErrors(() -> {
			authorisationRequestApi.cancelAuthorisation(flightOperationId)
				.block(REST_TIMEOUT);
			LOGGER.info("Cancel Flug {}", flightOperationId);
			return waitForAuthorizationStatusUpdate(flightOperationId, Set.of(AuthorisationStatusEnumDto.CANCELLED));
		});
	}

	private AuthorisationRequestResponseDto waitForAuthorizationStatusUpdate(String flightOperationId, Collection<AuthorisationStatusEnumDto> expectedStatus) {
		AuthorisationRequestResponseDto result = null;
		Instant start = Instant.now();
		do {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				LOGGER.info("Lass mich schlafen", e);
			}
			try {
				LOGGER.info("Frage Infos für Flug {} an", flightOperationId);
				result = getAuthorization(flightOperationId);
			} catch (IllegalStateException ex) {
				LOGGER.error(ex.getMessage());
			}
		} while (
			result != null && !expectedStatus.contains(result.getStatus().getAuthorisationStatus())
				&& (Instant.now().isBefore(start.plus(STATUS_CHANGE_TIMEOUT))));

		return result;
	}

	private <T> T executeAndLogErrors(Callable<T> lambda) {
		try {
			return lambda.call();
		} catch (WebClientResponseException e) {
			LOGGER.error("WebClientResponseException: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
			throw e;
		} catch (WebClientRequestException e) {
			LOGGER.error("WebClientRequestException: " + e.getMessage());
			throw e;
		} catch (IllegalStateException e) {
			LOGGER.error("Illegal State.. Timeout?", e);
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
