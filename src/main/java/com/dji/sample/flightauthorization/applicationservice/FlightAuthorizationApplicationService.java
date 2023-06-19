package com.dji.sample.flightauthorization.applicationservice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.api.command.CreateFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.api.view.FlightAuthorizationListView;
import com.dji.sample.flightauthorization.config.FlightAuthorizationConfigurationProperties;
import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.domain.service.FlightAuthorizationService;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.command.OperationalVolumeCommand;
import com.dji.sample.flightauthorization.ussp.command.SubmitFlightAuthorizationRequestCommand;
import com.dji.sample.flightauthorization.ussp.command.UnmannedAircraftCommand;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
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

public class FlightAuthorizationApplicationService {

	private final WaylineService waylineService;
	private final FlightAuthorizationService flightAuthorizationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;
	private final IDeviceService deviceService;

	private final FlightAuthorizationConfigurationProperties configurationProperties;

	public FlightAuthorizationApplicationService(
		WaylineService waylineService,
		FlightAuthorizationService flightAuthorizationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository,
		IDeviceService deviceService,
		FlightAuthorizationConfigurationProperties configurationProperties) {
		this.waylineService = waylineService;
		this.flightAuthorizationService = flightAuthorizationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
		this.deviceService = deviceService;
		this.configurationProperties = configurationProperties;
	}

	public FlightAuthorizationRequestView submitRequest(String workspaceId, String username,
		CreateFlightAuthorizationRequestCommand command) throws SubmissionFailedException {
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, command.getWaylineId());

			ResponseEntity<String> submissionResponse = usspFlightAuthorizationRepository.submitRequest(
				convertDataToSubmissionCommand(command, wayline));

			if (submissionResponse.getStatusCode() != HttpStatus.OK) {
				throw new SubmissionFailedException(submissionResponse.getStatusCode(),
					"Submission returned StatusCode " + submissionResponse.getStatusCode());
			}

			FlightAuthorization flightAuthorization = flightAuthorizationService.save(
				new FlightAuthorization(
					Name.of(username),
					WorkspaceId.of(workspaceId),
					WaylineFileId.of(command.getWaylineId()),
					command.getTitle(),
					command.getDescription(),
					command.getTakeoffTime(),
					command.getLandingTime(),
					command.getModeOfOperation(),
					USSPFlightOperationId.of(submissionResponse.getBody())
				));

			//TODO: either keep fetching or wait 5 seconds to pull status
			FlightAuthorizationRequestView flightRequestSubmission = usspFlightAuthorizationRepository
				.findByFlightOperationId(submissionResponse.getBody()).getBody();

			flightAuthorization.setAuthorisationStatus(
				flightRequestSubmission.getStatus().getAuthorisationStatus());
			flightAuthorization.setActivationStatus(
				flightRequestSubmission.getActivationStatus().getActivationStatus());
			flightAuthorizationService.save(flightAuthorization);

			return flightRequestSubmission;
		} catch (WaylineReadException e) {
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Failed to read Wayline file.");
		}
	}

	public List<FlightAuthorizationListView> getAllRequests() {
		return flightAuthorizationService.getAll()
			.stream()
			.map(FlightAuthorizationListView::new)
			.collect(Collectors.toList());
	}

	public ResponseEntity<FlightAuthorizationRequestView> getRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		return usspFlightAuthorizationRepository.findByFlightOperationId(
			authorization.getUsspFlightOperationId().toString());
	}

	public void cancelRequest(Long id) {
		FlightAuthorization authorization = flightAuthorizationService.get(id);
		usspFlightAuthorizationRepository.cancelByFlightOperationId(
			authorization.getUsspFlightOperationId().toString());
	}

	private SubmitFlightAuthorizationRequestCommand convertDataToSubmissionCommand(
		CreateFlightAuthorizationRequestCommand command, Wayline wayline) {

		OperationalVolumeCommand operationalVolumeCommand = OperationalVolumeCommand.builder()
			.area(wayline.getOperationalVolume().getArea())
			.minHeightInMeter(wayline.getOperationalVolume().getMinHeightInMeter())
			.maxHeightInMeter(wayline.getOperationalVolume().getMaxHeightInMeter())
			.build();

		return SubmitFlightAuthorizationRequestCommand
			.builder()
			.uasOperatorRegistrationNumber(command.getUasOperatorRegistrationNumber())
			.title(command.getTitle().toString())
			.description(command.getDescription().toString())
			.takeOffTime(command.getTakeoffTime())
			.landingTime(command.getLandingTime())
			.operationalVolume(operationalVolumeCommand)
			.modeOfOperation(command.getModeOfOperation())
			.typeOfFlight(TypeOfFlight.STANDARD)
			.unmannedAircrafts(getUnmannedAircraftCommands(command.getUasSerialNumber()))
			.correlationId(null)
			.safetyLandingPoints(null)
			.flightPath(wayline.getFlightPath())
			.build();
	}

	private List<UnmannedAircraftCommand> getUnmannedAircraftCommands(String serialNumber) {
		if(configurationProperties.isMockDevices()){
			return List.of(
				convertDeviceToCommand(DeviceDTO.builder()
					.registrationNumber("DJI.TEST-123")
					.deviceSn("321-456-n67-1-2")
					.build()));
		}
		return deviceService.getDevicesByParams(
				DeviceQueryParam.builder()
					.deviceSn(serialNumber)
					.build())
			.stream()
			.map(this::convertDeviceToCommand)
			.collect(Collectors.toList());
	}

	private UnmannedAircraftCommand convertDeviceToCommand(DeviceDTO device) {
		return UnmannedAircraftCommand.builder()
			.registrationNumber(device.getRegistrationNumber())
			.applicableEmergencyForConnectivityLoss("ELP")
			.category(UASCategory.SPECIFIC)
			.identificationTechnology(UASIdentificationTechnology.WIFI)
			.serialnumber(device.getDeviceSn())
			.uavClass(UAVClass.C2)
			.enduranceInMinutes(100)
			.build();
	}
}
