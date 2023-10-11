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

import de.hhlasky.uassimulator.api.ussp.dto.ActivationRequestResponseDto;
import de.hhlasky.uassimulator.api.ussp.dto.ActivationStatusEnumDto;
import de.hhlasky.uassimulator.api.ussp.sender.FlightActivationsApi;

@Service
public class ActivationRequestProxy {
	private static final Duration REST_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
	private static final Duration STATUS_CHANGE_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivationRequestProxy.class);

	private final FlightActivationsApi activationApi;

	public ActivationRequestProxy(FlightActivationsApi activationApi) {
		this.activationApi = activationApi;
	}

	public ActivationRequestResponseDto getActivationRequest(String flightOperationId) {
		return executeAndLogErrors(() -> {
			ActivationRequestResponseDto result = activationApi.getSingleActivation(flightOperationId).block(REST_TIMEOUT);
			LOGGER.info("Flug {} abgefragt", flightOperationId);
			return result;
		});
	}

	public ActivationRequestResponseDto activateApprovalRequest(String flightOperationId) {
		return executeAndLogErrors(() -> {
			activationApi.requestActivation(flightOperationId)
				.block(REST_TIMEOUT);
			LOGGER.info("Activate Flug {}", flightOperationId);
			return waitForActivationStatusUpdate(flightOperationId, Set.of(ActivationStatusEnumDto.ACTIVATED, ActivationStatusEnumDto.REJECTED));
		});
	}

	public ActivationRequestResponseDto deactivateApprovalRequest(String flightOperationId) {
		return executeAndLogErrors(() -> {
			activationApi.requestDeactivation(flightOperationId)
				.block(REST_TIMEOUT);
			LOGGER.info("Deactivate Flug {}", flightOperationId);
			return waitForActivationStatusUpdate(flightOperationId, Set.of(ActivationStatusEnumDto.DEACTIVATED));
		});
	}

	private ActivationRequestResponseDto waitForActivationStatusUpdate(String flightOperationId, Collection<ActivationStatusEnumDto> expectedStatus) {
		ActivationRequestResponseDto result;
		Instant start = Instant.now();
		do {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				LOGGER.info("Lass mich schlafen", e);
			}
			LOGGER.info("Frage Aktivierungsstatus für Flug {} an", flightOperationId);
			result = getActivationRequest(flightOperationId);
		} while (
			!expectedStatus.contains(result.getStatus().getActivationStatus())
				&& (Instant.now().isBefore(start.plus(STATUS_CHANGE_TIMEOUT))));

		return result;
	}

	private <T> T executeAndLogErrors(Callable<T> lambda) {
		try {
			return lambda.call();
		} catch (WebClientResponseException e) {
			LOGGER.error("WebClientResponseException: " + e.getResponseBodyAsString());
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

