package com.dji.sample.flightauthorization.applicationservice;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.dji.sample.flightauthorization.api.ussp.sender.ActivationRequestProxy;
import com.dji.sample.flightauthorization.api.ussp.sender.AuthorizationProxy;
import com.dji.sample.flightauthorization.config.FlightOperationConfigurationProperties;
import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.service.FlightOperationService;
import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.ussp.USSPFlightAuthorizationRepository;
import com.dji.sample.flightauthorization.ussp.dto.common.UASOperatorDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.SafetyLandingPointDTO;
import com.dji.sample.flightauthorization.ussp.dto.request.WaypointDTO;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.domain.entity.Wayline;
import com.dji.sample.wayline.domain.exception.WaylineReadException;
import com.dji.sample.wayline.domain.service.WaylineService;

import de.hhlasky.uassimulator.api.ussp.dto.ActivationRequestResponseDto;
import de.hhlasky.uassimulator.api.ussp.dto.AltitudeDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestResponseDto;
import de.hhlasky.uassimulator.api.ussp.dto.OperationalVolumeItemDto;
import de.hhlasky.uassimulator.api.ussp.dto.UnmannedAircraftDto;

public class FlightOperationApplicationService {

	private final WaylineService waylineService;
	private final FlightOperationService flightOperationService;
	private final USSPFlightAuthorizationRepository usspFlightAuthorizationRepository;
	private final IDeviceService deviceService;

	private final AuthorizationProxy authorizationProxy;

	private final ActivationRequestProxy activationProxy;

	private final FlightOperationConfigurationProperties configurationProperties;

	private static final String DUMMY_AIRCRAFT_OPERATOR = "DE.HH-SI-001";

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightOperationApplicationService.class);

	public FlightOperationApplicationService(
		WaylineService waylineService,
		FlightOperationService flightOperationService,
		USSPFlightAuthorizationRepository usspFlightAuthorizationRepository,
		IDeviceService deviceService,
		AuthorizationProxy authorizationProxy, FlightOperationConfigurationProperties configurationProperties,
		ActivationRequestProxy activationProxy) {
		this.waylineService = waylineService;
		this.flightOperationService = flightOperationService;
		this.usspFlightAuthorizationRepository = usspFlightAuthorizationRepository;
		this.deviceService = deviceService;
		this.authorizationProxy = authorizationProxy;
		this.configurationProperties = configurationProperties;
		this.activationProxy = activationProxy;
	}

	public void submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, requestDto.getWaylineid());

			AuthorisationRequestDto authorisationRequestDto = convertDataToAuthorizationRequestDto(
				requestDto, wayline);
			AuthorisationRequestResponseDto response = authorizationProxy.requestAuthorizationAndWait(authorisationRequestDto);
			LOGGER.debug("RequestAuthorization successful");

			FlightOperation flightOperation = flightOperationService.save(
				new FlightOperation(
					Name.of(username),
					WorkspaceId.of(workspaceId),
					WaylineFileId.of(requestDto.getWaylineid()),
					Title.of(requestDto.getTitle()),
					Description.of(requestDto.getDescription()),
					Instant.parse(requestDto.getTakeofftime()),
					Instant.parse(requestDto.getLandingtime()),
					ModeOfOperation.valueOf(requestDto.getModeofoperation()),
					USSPFlightOperationId.of(response.getFlightOperationId())
				));

			flightOperation.setAuthorisationStatus(response.getStatus().getAuthorisationStatus());
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

	private AuthorisationRequestDto convertDataToAuthorizationRequestDto(CreateFlightOperationRequestDTO createFlightOperationRequestDTO, Wayline wayline){

		AuthorisationRequestDto dto = new AuthorisationRequestDto();
		dto.setCorrelationId(null);
		dto.setTitle(createFlightOperationRequestDTO.getTitle().toString());
		dto.setDescription(createFlightOperationRequestDTO.getDescription().toString());
		OperationalVolumeItemDto operationalVolumeItemDto = this.getOperationalVolumeItemDto(wayline);
		dto.setOperationalVolumes(List.of(operationalVolumeItemDto));
		dto.setFlightPath(wayline.getFlightPath());
		dto.setModeOfOperation(AuthorisationRequestDto.ModeOfOperationEnum.BVLOS);
		dto.setTypeOfFlight(AuthorisationRequestDto.TypeOfFlightEnum.STANDARD);
		dto.setUasOperatorRegistrationNumber(DUMMY_AIRCRAFT_OPERATOR);
		UnmannedAircraftDto unmannedAircraftDto = this.getUnmannedAircraftDto();
		dto.setUnmannedAircrafts(List.of(unmannedAircraftDto));

		return dto;
	}

	private UnmannedAircraftDto getUnmannedAircraftDto() {
		UnmannedAircraftDto unmannedAircraftDto = new UnmannedAircraftDto();
		unmannedAircraftDto.setCategory(UnmannedAircraftDto.CategoryEnum.CERTIFIED);
		unmannedAircraftDto.serialnumber("DJICloudSerialNumber");
		unmannedAircraftDto.setApplicableEmergencyForConnectivityLoss("phone");
		unmannedAircraftDto.setEnduranceInMinutes(60);
		unmannedAircraftDto.setIdentificationTechnology(UnmannedAircraftDto.IdentificationTechnologyEnum.FLARM);
		unmannedAircraftDto.setUavClass(UnmannedAircraftDto.UavClassEnum.C1);
		unmannedAircraftDto.setRegistrationNumber("dummyRegistration");
		return unmannedAircraftDto;
	}

	private OperationalVolumeItemDto getOperationalVolumeItemDto(Wayline wayline){
		LineString flightPath = wayline.getFlightPath();

		// https://docs.geotools.org/latest/userguide/library/jts/operation.html
		Polygon flightArea = (Polygon) flightPath.buffer(0.001);

		List<Coordinate> flightPathCoordinates = Arrays.asList(flightPath.getCoordinates());
		double minHeight = flightPathCoordinates.stream().map(Coordinate::getZ).min(Double::compare).get() - 25;
		double maxHeight = flightPathCoordinates.stream().map(Coordinate::getZ).max(Double::compare).get() + 25;

		OperationalVolumeItemDto operationalVolumeItemDto = new OperationalVolumeItemDto();
		operationalVolumeItemDto.setArea(this.calculatePolygon(wayline));
		operationalVolumeItemDto.setEPSG(OperationalVolumeItemDto.EPSGEnum._4326);
		operationalVolumeItemDto.setEarliestEntryTime(Instant.now().plusSeconds(5));
		operationalVolumeItemDto.setLatestExitTime(Instant.now().plusSeconds(3600));
		AltitudeDto minHeightDto = new AltitudeDto();
		minHeightDto.setReference(AltitudeDto.ReferenceEnum.HAE_WGS84);
		minHeightDto.setUnits(AltitudeDto.UnitsEnum.M);
		minHeightDto.setValue(minHeight);
		AltitudeDto maxHeightDto = new AltitudeDto();
		maxHeightDto.setReference(AltitudeDto.ReferenceEnum.HAE_WGS84);
		maxHeightDto.setUnits(AltitudeDto.UnitsEnum.M);
		maxHeightDto.setValue(maxHeight);
		operationalVolumeItemDto.setMinHeight(minHeightDto);
		operationalVolumeItemDto.setMaxHeight(maxHeightDto);
		return operationalVolumeItemDto;
	}

	private UASOperatorDTO getOperatorDto() {
		return UASOperatorDTO.builder()
			.operatorID("DE.HH-USSP-0000")
			.contactURL("https://test.dji-cloud.wps.de/localhost-nicht-hier/")
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

	private Polygon calculatePolygon(Wayline wayline){
		LineString flightPath = wayline.getFlightPath();

		// https://docs.geotools.org/latest/userguide/library/jts/operation.html
		return (Polygon) flightPath.buffer(0.001);
	}

	public void cancelRequest(String flightOperationId) throws SubmissionFailedException {
		try{
			AuthorisationRequestResponseDto response = authorizationProxy.cancelAuthorizationAndWait(flightOperationId);
			LOGGER.info("Cancel Request for FlightOperation {} successful", response.getFlightOperationId());
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public void activateFlight(String flightOperationId) throws SubmissionFailedException {
		try{
			ActivationRequestResponseDto response = activationProxy.activateApprovalRequest(flightOperationId);
			LOGGER.info("Activate Request for FlightOperation {} successful", response.getFlightOperationId());
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public void deactivateFlight(String flightOperationId) throws SubmissionFailedException {
		try{
			ActivationRequestResponseDto response = activationProxy.deactivateApprovalRequest(flightOperationId);
			LOGGER.info("Deactivate Request for FlightOperation {} successful", response.getFlightOperationId());
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
