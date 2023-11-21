package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.response.FlightOperationListDTO;
import com.dji.sample.flightauthorization.domain.service.FlightExecutionService;
import com.dji.sample.flightauthorization.domain.service.FlightPlanningService;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;

public class FlightOperationApplicationService {

	private final FlightPlanningService flightPlanningService;

	private final FlightExecutionService flightExecutionService;

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightOperationApplicationService.class);

	public FlightOperationApplicationService(
		FlightPlanningService flightPlanningService,
		FlightExecutionService flightExecutionService) {
		this.flightPlanningService = flightPlanningService;
		this.flightExecutionService = flightExecutionService;
	}

	public void submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		flightPlanningService.submitRequest(workspaceId, username, requestDto);
	}

	public List<FlightOperationListDTO> getAllRequests() {
		return flightPlanningService.getAll()
			.stream()
			.map(FlightOperationListDTO::new)
			.collect(Collectors.toList());
	}

	public void cancelRequest(String flightOperationId) throws SubmissionFailedException {
		flightPlanningService.cancelRequest(flightOperationId);
	}

	public void activateFlight(String flightOperationId) throws SubmissionFailedException {
		flightExecutionService.activateFlight(USSPFlightOperationId.of(flightOperationId));
	}

	public void deactivateFlight(String flightOperationId) throws SubmissionFailedException {
		flightExecutionService.deactivateFlight(USSPFlightOperationId.of(flightOperationId));
	}

	public void sendDroneTelemetryData(OsdSubDeviceReceiver osdData, String deviceSn) {
		flightExecutionService.sendDroneTelemetryData(osdData, deviceSn);
	}
}
