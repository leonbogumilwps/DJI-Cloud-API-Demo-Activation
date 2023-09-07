package com.dji.sample.flightauthorization.applicationservice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.response.FlightOperationListDTO;
import com.dji.sample.flightauthorization.api.ussp.sender.AuthorizationProxy;
import com.dji.sample.flightauthorization.config.FlightOperationConfigurationProperties;
import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.service.FlightOperationService;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.dto.common.GeofenceDto;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.dto.common.UASCategory;
import com.dji.sample.flightauthorization.ussp.dto.common.UASIdentificationTechnology;
import com.dji.sample.flightauthorization.ussp.dto.common.UASOperatorDTO;
import com.dji.sample.flightauthorization.ussp.dto.common.UAVClass;
import com.dji.sample.flightauthorization.ussp.dto.common.UAVDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.SafetyLandingPointDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.SubmitFlightAuthorizationRequestDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.WaypointDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.domain.entity.Wayline;
import com.dji.sample.wayline.domain.exception.WaylineReadException;
import com.dji.sample.wayline.domain.service.WaylineService;

import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestResponseDto;

public class FlightOperationApplicationService {

	private final WaylineService waylineService;
	private final FlightOperationService flightOperationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;
	private final IDeviceService deviceService;

	private final AuthorizationProxy authorizationProxy;

	private final FlightOperationConfigurationProperties configurationProperties;

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightOperationApplicationService.class);

	public FlightOperationApplicationService(
		WaylineService waylineService,
		FlightOperationService flightOperationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository,
		IDeviceService deviceService,
		AuthorizationProxy authorizationProxy, FlightOperationConfigurationProperties configurationProperties) {
		this.waylineService = waylineService;
		this.flightOperationService = flightOperationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
		this.deviceService = deviceService;
		this.authorizationProxy = authorizationProxy;
		this.configurationProperties = configurationProperties;
	}

	public void submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, requestDto.getWaylineId());

			SubmitFlightAuthorizationRequestDTO submitFlightAuthorizationRequestDTO = convertDataToSubmissionDTO(
				requestDto, wayline);
			AuthorisationRequestDto request = new AuthorisationRequestDto(); //TODO
			AuthorisationRequestResponseDto response = authorizationProxy.requestAuthorizationAndWait(request);
			//ResponseEntity<String> submissionResponse = usspFlightAuthorizationRepository.submitRequest(submitFlightAuthorizationRequestDTO);
			LOGGER.debug("RequestAuthorization successful");

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
					USSPFlightOperationId.of(response.getFlightOperationId())
				));

			//TODO: either keep fetching or wait 5 seconds to pull status
			//FlightOperationDetailDTO flightRequestSubmission = usspFlightAuthorizationRepository
			//	.findByFlightOperationId(submissionResponse.getBody()).getBody();

			// Status: ACCEPTED, REJECTED, PENDING
			// ApprovalStatusUASDTO status = usspFlightAuthorizationRepository.findStatusByFlightOperationId(
			// submissionResponse.getBody()).getBody();

			flightOperation.setApprovalRequestStatus(response.getStatus().getAuthorisationStatus());
			flightOperationService.save(flightOperation);
		} catch (WaylineReadException e) {
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Failed to read Wayline file.");
		}
	 	catch (WebClientResponseException e) {
			LOGGER.info("Request HTTP Error: " + e.getResponseBodyAsString());
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Request HTTP Error");
		} catch (WebClientRequestException e) {
			LOGGER.info("Request Server Error: " + e.getMessage());
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Request Server Error");
		} catch (IllegalStateException e) {
			LOGGER.info("Request IllegalStateException: " + e.getMessage());
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, "Request IllegalStateException");
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

	private SubmitFlightAuthorizationRequestDTO convertDataToSubmissionDTO(
		CreateFlightOperationRequestDTO createFlightOperationRequestDTO, Wayline wayline) {

		return SubmitFlightAuthorizationRequestDTO
			.builder()
			.correlationId(null)
			.title(createFlightOperationRequestDTO.getTitle().toString())
			.description(createFlightOperationRequestDTO.getDescription().toString())
			.takeOffTime(createFlightOperationRequestDTO.getTakeoffTime())
			.landingTime(createFlightOperationRequestDTO.getLandingTime())
			.typeOfFlight(TypeOfFlight.SPECIAL_OPERATIONS)
			.flightMode(createFlightOperationRequestDTO.getModeOfOperation())
			.operator(this.getOperatorDto())
			.waypoints(this.calculateWaypoints(wayline))
			.safetyLandingPoints(this.calculateSafetyLandingPoints(wayline))
			.geofence(this.calculateGeofence(wayline))
			.uav(this.getUAVDto(createFlightOperationRequestDTO.getUasSerialNumber()))
			.build();
	}

	private UASOperatorDTO getOperatorDto() {
		return UASOperatorDTO.builder()
			.operatorID("DE.HH-USSP-0000")
			.contactURL("https://test.dji-cloud.wps.de/localhost-nicht-hier/")
			.build();
	}

	//"2336-55123-X123"
	private UAVDTO getUAVDto(String serialNumber) {
		if (configurationProperties.isMockDevices()) {
			return
				convertDeviceToCommand(DeviceDTO.builder()
					.registrationNumber("DE.WPS-TEST-1234")
					.deviceSn(serialNumber)
					.build());
		}
		return deviceService.getDevicesByParams(
				DeviceQueryParam.builder()
					.deviceSn(serialNumber)
					.build())
			.stream()
			.map(this::convertDeviceToCommand)
			.collect(Collectors.toList()).get(0);
	}

	private UAVDTO convertDeviceToCommand(DeviceDTO device) {
		return UAVDTO.builder()
			.serialnumber(device.getDeviceSn())
			.registrationId(device.getRegistrationNumber())
			.category(UASCategory.SPECIFIC)
			.uavClass(UAVClass.C2)
			.identificationTechnology(UASIdentificationTechnology.WIFI)
			.expectedConnectivityMethod(true)
			.endurance(300)
			.applicableEmergencyForConnectivityLoss("ELP")
			.build();
	}

	private List<WaypointDTO> calculateWaypoints(Wayline wayline) {
		LineString flightPath = wayline.getFlightPath();
		return Arrays.stream(flightPath.getCoordinates())
			.map(this::convertCoordinateToWaypoint)
			.collect(Collectors.toList());
	}

	private WaypointDTO convertCoordinateToWaypoint(Coordinate coordinate) {
		return WaypointDTO.builder()
			.position(new Point(coordinate.x, coordinate.y))
			.altitudeInMeters(coordinate.z)
			.cruisingSpeed(5.0)
			.build();
	}

	private List<SafetyLandingPointDTO> calculateSafetyLandingPoints(Wayline wayline) {
		LineString flightPath = wayline.getFlightPath();
		Coordinate firstCoordinate = flightPath.getCoordinates()[0];
		return List.of(
			SafetyLandingPointDTO.builder()
				.position(new Point(firstCoordinate.x, firstCoordinate.y))
				.radiusInMeter(1)
				.build()
		);
	}

	private GeofenceDto calculateGeofence(Wayline wayline) {
		LineString flightPath = wayline.getFlightPath();

		// https://docs.geotools.org/latest/userguide/library/jts/operation.html
		Polygon flightArea = (Polygon) flightPath.buffer(0.001);

		List<Coordinate> flightPathCoordinates = Arrays.asList(flightPath.getCoordinates());
		double minHeight = flightPathCoordinates.stream().map(Coordinate::getZ).min(Double::compare).get();
		double maxHeight = flightPathCoordinates.stream().map(Coordinate::getZ).max(Double::compare).get();

		return GeofenceDto.builder()
			.area(createPolygonGeoJSON(flightArea))
			.minHeightInMeter(minHeight)
			.maxHeightInMeter(maxHeight)
			.build();
	}

	private org.geojson.Polygon createPolygonGeoJSON(Polygon polygon) {
		if (polygon.getNumInteriorRing() > 0) {
			throw new RuntimeException("Polygone mit Löchern werden momentan nicht unterstützt.");
		}
		LngLatAlt[] lngLatAlts = parseCoordinates(polygon.getCoordinates());
		return new org.geojson.Polygon(Arrays.asList(lngLatAlts));
	}

	private LngLatAlt[] parseCoordinates(Coordinate[] coordinates) {
		LngLatAlt[] lngLatAlts = new LngLatAlt[coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			lngLatAlts[i] = new LngLatAlt(coordinates[i].x, coordinates[i].y, coordinates[i].z);
		}
		return lngLatAlts;
	}
}
