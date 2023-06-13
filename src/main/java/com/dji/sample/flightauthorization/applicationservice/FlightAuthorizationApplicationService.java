package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.api.view.FlightAuthorizationListView;
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

	public void cancelRequest() {
	}
}
