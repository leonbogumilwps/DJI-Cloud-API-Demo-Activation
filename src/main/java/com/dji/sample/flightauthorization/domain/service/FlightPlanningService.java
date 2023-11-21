package com.dji.sample.flightauthorization.domain.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.ussp.sender.AuthorizationProxy;
import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.wayline.domain.entity.Wayline;
import com.dji.sample.wayline.domain.exception.WaylineReadException;
import com.dji.sample.wayline.domain.service.WaylineService;

import de.hhlasky.uassimulator.api.ussp.dto.AltitudeDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestDto;
import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationRequestResponseDto;
import de.hhlasky.uassimulator.api.ussp.dto.OperationalVolumeItemDto;
import de.hhlasky.uassimulator.api.ussp.dto.UnmannedAircraftDto;

public class FlightPlanningService {

	private final FlightOperationRepository repository;

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightPlanningService.class);

	private static final String DUMMY_AIRCRAFT_OPERATOR = "DE.HH-SI-001";

	private final WaylineService waylineService;

	private final AuthorizationProxy authorizationProxy;

	public FlightPlanningService(FlightOperationRepository repository, WaylineService waylineService, AuthorizationProxy authorizationProxy) {
		this.repository = repository;
		this.waylineService = waylineService;
		this.authorizationProxy = authorizationProxy;
	}

	public FlightOperation get(Long id) {
		return repository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	public List<FlightOperation> getAll() {
		return repository.findAll();
	}

	public FlightOperation save(FlightOperation flightOperation) {
		return repository.save(flightOperation);
	}

	public FlightOperation getByFlightOperationId(USSPFlightOperationId flightOperationId){
		return repository.findFlightOperationByUsspFlightOperationId(flightOperationId).orElseThrow(EntityNotFoundException::new);
	}

	public void submitRequest(String workspaceId, String username,
		CreateFlightOperationRequestDTO requestDto) throws SubmissionFailedException {
		try {
			Wayline wayline = waylineService.getWayline(workspaceId, requestDto.getWaylineid());

			AuthorisationRequestDto authorisationRequestDto = convertDataToAuthorizationRequestDto(
				requestDto, wayline);
			AuthorisationRequestResponseDto response = authorizationProxy.requestAuthorizationAndWait(authorisationRequestDto);
			LOGGER.debug("RequestAuthorization successful");

			FlightOperation flightOperation = this.save(
				new FlightOperation(
					Name.of(username),
					requestDto.getUasserialnumber(),
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
			this.save(flightOperation);
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
	private AuthorisationRequestDto convertDataToAuthorizationRequestDto(CreateFlightOperationRequestDTO createFlightOperationRequestDTO, Wayline wayline){

		AuthorisationRequestDto dto = new AuthorisationRequestDto();
		dto.setCorrelationId(null);
		dto.setTitle(createFlightOperationRequestDTO.getTitle());
		dto.setDescription(createFlightOperationRequestDTO.getDescription());
		OperationalVolumeItemDto operationalVolumeItemDto = this.getOperationalVolumeItemDto(wayline);
		dto.setOperationalVolumes(List.of(operationalVolumeItemDto));
		dto.setFlightPath(wayline.getFlightPath());
		dto.setModeOfOperation(AuthorisationRequestDto.ModeOfOperationEnum.BVLOS);
		dto.setTypeOfFlight(AuthorisationRequestDto.TypeOfFlightEnum.STANDARD);
		dto.setUasOperatorRegistrationNumber(DUMMY_AIRCRAFT_OPERATOR);
		UnmannedAircraftDto unmannedAircraftDto = this.getUnmannedAircraftDto(createFlightOperationRequestDTO.getUasserialnumber());
		dto.setUnmannedAircrafts(List.of(unmannedAircraftDto));

		return dto;
	}

	private OperationalVolumeItemDto getOperationalVolumeItemDto(Wayline wayline){
		LineString flightPath = wayline.getFlightPath();

		List<Coordinate> flightPathCoordinates = Arrays.asList(flightPath.getCoordinates());
		double minHeight = flightPathCoordinates.stream().map(Coordinate::getZ).min(Double::compare).get() - 25;
		double maxHeight = flightPathCoordinates.stream().map(Coordinate::getZ).max(Double::compare).get() + 25;

		OperationalVolumeItemDto operationalVolumeItemDto = new OperationalVolumeItemDto();
		operationalVolumeItemDto.setArea(this.calculatePolygon(wayline));
		operationalVolumeItemDto.setEPSG(OperationalVolumeItemDto.EPSGEnum._4326);
		operationalVolumeItemDto.setEarliestEntryTime(Instant.now().plusSeconds(5)); //TODO
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

	private Polygon calculatePolygon(Wayline wayline){
		LineString flightPath = wayline.getFlightPath();

		// https://docs.geotools.org/latest/userguide/library/jts/operation.html
		return (Polygon) flightPath.buffer(0.003);
	}

	private UnmannedAircraftDto getUnmannedAircraftDto(String uasSerialNumber) {
		UnmannedAircraftDto unmannedAircraftDto = new UnmannedAircraftDto();
		unmannedAircraftDto.setCategory(UnmannedAircraftDto.CategoryEnum.OPEN);
		unmannedAircraftDto.serialnumber(uasSerialNumber);
		unmannedAircraftDto.setApplicableEmergencyForConnectivityLoss("phone");
		unmannedAircraftDto.setEnduranceInMinutes(60);
		unmannedAircraftDto.setIdentificationTechnology(UnmannedAircraftDto.IdentificationTechnologyEnum.ADS_B);
		unmannedAircraftDto.setUavClass(UnmannedAircraftDto.UavClassEnum.C3);
		unmannedAircraftDto.setRegistrationNumber("EASARegistration");
		return unmannedAircraftDto;
	}

	public void cancelRequest(String flightOperationId) throws SubmissionFailedException {
		try{
			FlightOperation flightOperation = this.getByFlightOperationId(USSPFlightOperationId.of(flightOperationId));
			AuthorisationRequestResponseDto response = authorizationProxy.cancelAuthorizationAndWait(flightOperationId);
			LOGGER.info("Cancel Request for FlightOperation {} successful", response.getFlightOperationId());
			flightOperation.cancel();
			this.save(flightOperation);
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
