package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.api.command.CreateFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.api.view.FlightAuthorizationListView;
import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.view.FlightAuthorizationRequestView;
import com.dji.sample.wayline.domain.service.WaylineService;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.flightauthorization.domain.service.FlightAuthorizationService;

public class FlightAuthorizationApplicationService {

	private final WaylineService waylineService;
	private final FlightAuthorizationService flightAuthorizationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;

	public FlightAuthorizationApplicationService(
		WaylineService waylineService,
		FlightAuthorizationService flightAuthorizationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository) {
		this.waylineService = waylineService;
		this.flightAuthorizationService = flightAuthorizationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
	}

	public void submitRequest(String workspaceId, CreateFlightAuthorizationRequestCommand command) {
		//TODO: get wayline entity, waylinefileservice reads and converts it
		waylineService.getWayline(workspaceId,command.getWaylineId());
		//TODO: Convert wayline mission to SubmitRequestCommand
		//TODO: Submit request to USSP
		//TODO: on success pull submitted request at USSP
		//TODO: Create FlightAuthorization Entity and save it in repository
	}

	public List<FlightAuthorizationListView> getAllRequests() {
		return flightAuthorizationService.getAll()
			.stream()
			.map(FlightAuthorizationListView::new)
			.collect(Collectors.toList());
	}

	public ResponseEntity<FlightAuthorizationRequestView> getRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		return usspFlightAuthorizationRepository.findByFlightOperationId(authorization.getUsspId().toString());
	}

	public void cancelRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		usspFlightAuthorizationRepository.cancelByFlightOperationId(authorization.getUsspId().toString());
	}
}
