package com.dji.sample.flightauthorization.applicationservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.view.FlightAuthorizationRequestView;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.flightauthorization.domain.service.FlightAuthorizationService;

public class FlightAuthorizationApplicationService {

	private final IWaylineFileService waylineFileService;
	private final FlightAuthorizationService flightAuthorizationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;

	public FlightAuthorizationApplicationService(
		IWaylineFileService waylineFileService,
		FlightAuthorizationService flightAuthorizationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository) {
		this.waylineFileService = waylineFileService;
		this.flightAuthorizationService = flightAuthorizationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
	}

	public void submitRequest() {
	}

	public void getRequests() {
	}

	public ResponseEntity<FlightAuthorizationRequestView> getRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		return usspFlightAuthorizationRepository.findByFlightOperationId(authorization.getUsspId().toString());
	}

	public void cancelRequest() {
	}
}
