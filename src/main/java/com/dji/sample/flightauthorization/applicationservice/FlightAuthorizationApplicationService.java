package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.MultiPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.api.command.CreateFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.api.view.FlightAuthorizationListView;
import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.command.OperationalVolumeCommand;
import com.dji.sample.flightauthorization.ussp.command.SubmitFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.ussp.command.UnmannedAircraftCommand;
import com.dji.sample.flightauthorization.ussp.view.FlightAuthorizationRequestView;
import com.dji.sample.flightauthorization.ussp.view.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.view.UASCategory;
import com.dji.sample.flightauthorization.ussp.view.UASIdentificationTechnology;
import com.dji.sample.flightauthorization.ussp.view.UAVClass;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.domain.entity.Wayline;
import com.dji.sample.wayline.domain.exception.WaylineReadException;
import com.dji.sample.wayline.domain.service.WaylineService;
import com.dji.sample.flightauthorization.domain.service.FlightAuthorizationService;

public class FlightAuthorizationApplicationService {

	private final WaylineService waylineService;
	private final FlightAuthorizationService flightAuthorizationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;
	private final IDeviceService deviceService;

	public FlightAuthorizationApplicationService(
		WaylineService waylineService,
		FlightAuthorizationService flightAuthorizationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository,
		IDeviceService deviceService) {
		this.waylineService = waylineService;
		this.flightAuthorizationService = flightAuthorizationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
		this.deviceService = deviceService;
	}

	public ResponseEntity<FlightAuthorizationRequestView> submitRequest(String workspaceId, String username,
		CreateFlightAuthorizationRequestCommand command) {
		// How to make it better: save wayline entity on FlightAuthorization Entity in a PostGIS DB
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, command.getWaylineId());

			//TODO: nicht zuerst speichern, correlationId nicht nutzen
			FlightAuthorization flightAuthorization = flightAuthorizationService.save(
				new FlightAuthorization(
					Name.of(username),
					WorkspaceId.of(workspaceId),
					WaylineFileId.of(command.getWaylineId()),
					command.getTitle(),
					command.getDescription(),
					command.getTakeoffTime(),
					command.getLandingTime(),
					command.getModeOfOperation()
				));

			// There are none
			MultiPoint safetyLandingPoints = null;

			List<UnmannedAircraftCommand> unmannedAircrafts = deviceService.getDevicesByParams(
					DeviceQueryParam.builder()
						.deviceSn(command.getUasSerialNumber())
						.build())
				.stream()
				.map(this::convertDeviceToCommand)
				.collect(Collectors.toList());

			OperationalVolumeCommand operationalVolumeCommand = OperationalVolumeCommand.builder()
				.area(wayline.getOperationalVolume().getArea())
				.minHeightInMeter(wayline.getOperationalVolume().getMinHeightInMeter())
				.maxHeightInMeter(wayline.getOperationalVolume().getMaxHeightInMeter())
				.build();

			SubmitFlightAuthorizationRequestCommand submitToUsspCommand = SubmitFlightAuthorizationRequestCommand
				.builder()
				.uasOperatorRegistrationNumber(command.getUasOperatorRegistrationNumber())
				.title(command.getTitle().toString())
				.description(command.getDescription().toString())
				.takeOffTime(command.getTakeoffTime())
				.landingTime(command.getLandingTime())
				.operationalVolume(operationalVolumeCommand)
				.modeOfOperation(command.getModeOfOperation())
				.typeOfFlight(TypeOfFlight.STANDARD)
				.unmannedAircrafts(unmannedAircrafts)
				.correlationId(flightAuthorization.getId().toString())
				.safetyLandingPoints(safetyLandingPoints)
				.flightPath(wayline.getFlightPath())
				.build();

			ResponseEntity<String> submissionResponse = usspFlightAuthorizationRepository.submitRequest(
				submitToUsspCommand);
			if (submissionResponse.getStatusCode() != HttpStatus.OK) {
				// submit failed, roll back (delete entity)
			}
			ResponseEntity<FlightAuthorizationRequestView> flightRequestSubmission = usspFlightAuthorizationRepository
				.findByFlightOperationId(submissionResponse.getBody());

			flightAuthorization.setUsspFlightOperationId(USSPFlightOperationId.of(flightRequestSubmission.getBody().getFlightOperationId()));
			flightAuthorization.setAuthorisationStatus(
				flightRequestSubmission.getBody().getStatus().getAuthorisationStatus());
			flightAuthorization.setActivationStatus(
				flightRequestSubmission.getBody().getActivationStatus().getActivationStatus());
			flightAuthorizationService.save(flightAuthorization);

			return flightRequestSubmission;
		} catch (WaylineReadException e) {
		}
		return ResponseEntity.internalServerError().build();
	}

	public List<FlightAuthorizationListView> getAllRequests() {
		return flightAuthorizationService.getAll()
			.stream()
			.map(FlightAuthorizationListView::new)
			.collect(Collectors.toList());
	}

	public ResponseEntity<FlightAuthorizationRequestView> getRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		return usspFlightAuthorizationRepository.findByFlightOperationId(authorization.getUsspFlightOperationId().toString());
	}

	public void cancelRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		usspFlightAuthorizationRepository.cancelByFlightOperationId(authorization.getUsspFlightOperationId().toString());
	}

	private UnmannedAircraftCommand convertDeviceToCommand(DeviceDTO device) {
		return UnmannedAircraftCommand.builder()
			.registrationNumber(device.getRegistrationNumber())
			.applicableEmergencyForConnectivityLoss("TBD")
			.category(UASCategory.SPECIFIC)
			.identificationTechnology(UASIdentificationTechnology.WIFI)
			.serialnumber(device.getDeviceSn())
			.uavClass(UAVClass.C2)
			.enduranceInMinutes(100)
			.build();
	}
}
