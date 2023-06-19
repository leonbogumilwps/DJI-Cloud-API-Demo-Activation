package com.dji.sample.flightauthorization.applicationservice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.response.FlightOperationListDTO;
import com.dji.sample.flightauthorization.config.FlightOperationConfigurationProperties;
import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.service.FlightOperationService;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.dto.common.OperationalVolume;
import com.dji.sample.flightauthorization.ussp.dto.common.UnmannedAircraft;
import com.dji.sample.flightauthorization.ussp.dto.request.SubmitFlightAuthorizationRequestDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.dto.common.UASCategory;
import com.dji.sample.flightauthorization.ussp.dto.common.UASIdentificationTechnology;
import com.dji.sample.flightauthorization.ussp.dto.common.UAVClass;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.domain.entity.Wayline;
import com.dji.sample.wayline.domain.exception.WaylineReadException;
import com.dji.sample.wayline.domain.service.WaylineService;

public class FlightOperationApplicationService {

	private final WaylineService waylineService;
	private final FlightOperationService flightOperationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;
	private final IDeviceService deviceService;

	private final FlightOperationConfigurationProperties configurationProperties;

	public FlightOperationApplicationService(
		WaylineService waylineService,
		FlightOperationService flightOperationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository,
		IDeviceService deviceService,
		FlightOperationConfigurationProperties configurationProperties) {
		this.waylineService = waylineService;
		this.flightOperationService = flightOperationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
		this.deviceService = deviceService;
		this.configurationProperties = configurationProperties;
	}

	public FlightOperationDetailDTO submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, requestDto.getWaylineId());

			SubmitFlightAuthorizationRequestDTO submitFlightAuthorizationRequestDTO = convertDataToSubmissionDTO(
				requestDto, wayline);
			ResponseEntity<String> submissionResponse = usspFlightAuthorizationRepository.submitRequest(
				submitFlightAuthorizationRequestDTO
			);

			if (submissionResponse.getStatusCode() != HttpStatus.OK) {
				throw new SubmissionFailedException(submissionResponse.getStatusCode(),
					"Submission returned StatusCode " + submissionResponse.getStatusCode());
			}

			FlightOperation flightOperation = flightOperationService.save(
				new FlightOperation(
					Name.of(username),
					WorkspaceId.of(workspaceId),
					WaylineFileId.of(requestDto.getWaylineId()),
					requestDto.getTitle(),
					requestDto.getDescription(),
					requestDto.getTakeoffTime(),
					requestDto.getLandingTime(),
					requestDto.getModeOfOperation(),
					USSPFlightOperationId.of(submissionResponse.getBody())
				));

			//TODO: either keep fetching or wait 5 seconds to pull status
			FlightOperationDetailDTO flightRequestSubmission = usspFlightAuthorizationRepository
				.findByFlightOperationId(submissionResponse.getBody()).getBody();

			flightOperation.setAuthorisationStatus(
				flightRequestSubmission.getStatus().getAuthorisationStatus());
			flightOperation.setActivationStatus(
				flightRequestSubmission.getActivationStatus().getActivationStatus());
			flightOperationService.save(flightOperation);

			return flightRequestSubmission;
		} catch (WaylineReadException e) {
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Failed to read Wayline file.");
		}
	}

	public List<FlightOperationListDTO> getAllRequests() {
		return flightOperationService.getAll()
			.stream()
			.map(FlightOperationListDTO::new)
			.collect(Collectors.toList());
	}

	public ResponseEntity<FlightOperationDetailDTO> getRequest(Long id) {
		FlightOperation authorization = flightOperationService.get(id);
		return usspFlightAuthorizationRepository.findByFlightOperationId(
			authorization.getUsspFlightOperationId().toString());
	}

	public void cancelRequest(Long id) {
		FlightOperation authorization = flightOperationService.get(id);
		usspFlightAuthorizationRepository.cancelByFlightOperationId(
			authorization.getUsspFlightOperationId().toString());
	}

	private SubmitFlightAuthorizationRequestDTO convertDataToSubmissionDTO(
		CreateFlightOperationRequestDTO createFlightOperationRequestDTO, Wayline wayline) {

		return SubmitFlightAuthorizationRequestDTO
			.builder()
			.uasOperatorRegistrationNumber(createFlightOperationRequestDTO.getUasOperatorRegistrationNumber())
			.title(createFlightOperationRequestDTO.getTitle().toString())
			.description(createFlightOperationRequestDTO.getDescription().toString())
			.takeOffTime(createFlightOperationRequestDTO.getTakeoffTime())
			.landingTime(createFlightOperationRequestDTO.getLandingTime())
			.operationalVolume(calculateOperationalVolume(wayline))
			.modeOfOperation(createFlightOperationRequestDTO.getModeOfOperation())
			.typeOfFlight(TypeOfFlight.STANDARD)
			.unmannedAircrafts(getUnmannedAircraftCommands(createFlightOperationRequestDTO.getUasSerialNumber()))
			.correlationId(null)
			.safetyLandingPoints(null)
			.flightPath(wayline.getFlightPath())
			.build();
	}

	private List<UnmannedAircraft> getUnmannedAircraftCommands(String serialNumber) {
		if (configurationProperties.isMockDevices()) {
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

	private UnmannedAircraft convertDeviceToCommand(DeviceDTO device) {
		return UnmannedAircraft.builder()
			.registrationNumber(device.getRegistrationNumber())
			.applicableEmergencyForConnectivityLoss("ELP")
			.category(UASCategory.SPECIFIC)
			.identificationTechnology(UASIdentificationTechnology.WIFI)
			.serialnumber(device.getDeviceSn())
			.uavClass(UAVClass.C2)
			.enduranceInMinutes(100)
			.build();
	}

	private OperationalVolume calculateOperationalVolume(Wayline wayline) {
		LineString flightPath = wayline.getFlightPath();

		// https://docs.geotools.org/latest/userguide/library/jts/operation.html
		Polygon flightArea = (Polygon) flightPath.buffer(10);

		List<Coordinate> flightPathCoordinates = Arrays.asList(flightPath.getCoordinates());
		double minHeight = flightPathCoordinates.stream().map(Coordinate::getZ).min(Double::compare).get();
		double maxHeight = flightPathCoordinates.stream().map(Coordinate::getZ).max(Double::compare).get();

		return OperationalVolume.builder()
			.area(flightArea)
			.minHeightInMeter(minHeight)
			.maxHeightInMeter(maxHeight)
			.build();
	}
}
