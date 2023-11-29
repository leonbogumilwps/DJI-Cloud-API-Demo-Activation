package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.response.FlightOperationListDTO;
import com.dji.sample.flightauthorization.domain.service.FlightOperationExecutionService;
import com.dji.sample.flightauthorization.domain.service.FlightOperationPlanningService;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;

public class FlightOperationApplicationService {

	private final FlightOperationPlanningService flightOperationPlanningService;

	private final FlightOperationExecutionService flightOperationExecutionService;

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightOperationApplicationService.class);

	public FlightOperationApplicationService(
		FlightOperationPlanningService flightOperationPlanningService,
		FlightOperationExecutionService flightOperationExecutionService) {
		this.flightOperationPlanningService = flightOperationPlanningService;
		this.flightOperationExecutionService = flightOperationExecutionService;
	}

	public void submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		flightOperationPlanningService.submitRequest(workspaceId, username, requestDto);
	}

	public void cancelRequest(String flightOperationId) throws SubmissionFailedException {
		flightOperationPlanningService.cancelRequest(flightOperationId);
	}

	public List<FlightOperationListDTO> getAllRequests() {
		return flightOperationPlanningService.getAll()
			.stream()
			.map(FlightOperationListDTO::new)
			.collect(Collectors.toList());
	}

	public void activateFlight(String flightOperationId) throws SubmissionFailedException {
		flightOperationExecutionService.activateFlight(USSPFlightOperationId.of(flightOperationId));
	}

	public void deactivateFlight(String flightOperationId) throws SubmissionFailedException {
		flightOperationExecutionService.deactivateFlight(USSPFlightOperationId.of(flightOperationId));
	}

	public void sendDroneTelemetryData(OsdSubDeviceReceiver osdData, String deviceSn) {
		flightOperationExecutionService.submitCurrentDroneState(osdData, deviceSn);
	}
}
