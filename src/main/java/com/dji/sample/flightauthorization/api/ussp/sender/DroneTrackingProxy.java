package com.dji.sample.flightauthorization.api.ussp.sender;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;

import de.hhlasky.uassimulator.api.ussp.dto.AltitudeDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationFlightDetailsClassificationDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationFlightDetailsDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationOperatorDetailsDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationTelemetryDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationTimestampDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationUasDetailsDto;
import de.hhlasky.uassimulator.api.ussp.dto.DroneTrackingInformationUasDetailsIdentificationDto;
import de.hhlasky.uassimulator.api.ussp.sender.FlightTrackingApi;

@Service
public class DroneTrackingProxy {
	private static final Logger LOGGER = getLogger(DroneTrackingProxy.class);

	private final FlightTrackingApi trackingApi;

	public DroneTrackingProxy(FlightTrackingApi trackingApi) {
		this.trackingApi = trackingApi;
	}

	private static DroneTrackingInformationDto toDto(OsdSubDeviceReceiver droneState, FlightOperation flightOperation) {
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326); //TODO
		Coordinate coordinate = new Coordinate(droneState.getLongitude(), droneState.getLatitude());
		Point point = factory.createPoint(coordinate);
		Optional<Float> flightDirection = Optional.ofNullable(droneState.getAttitudeHead());
		Instant now = Instant.now();

		return new DroneTrackingInformationDto()
			.timestamp(new DroneTrackingInformationTimestampDto()
				.value(now.toString()) //TODO: Das sollte eigentlich aus dem Drohnenstatus gelesen werden
				.format(DroneTrackingInformationTimestampDto.FormatEnum.RFC3339)
				.accuracy(0))
			.flightDetails(new DroneTrackingInformationFlightDetailsDto()
				.flightOperationId(flightOperation.getUsspFlightOperationId().toString())
				.classification(new DroneTrackingInformationFlightDetailsClassificationDto()
					.category(DroneTrackingInformationFlightDetailsClassificationDto.CategoryEnum.OPEN)
					.propertyClass(DroneTrackingInformationFlightDetailsClassificationDto.PropertyClassEnum.UNDEFINED)))
			.operatorDetails(new DroneTrackingInformationOperatorDetailsDto()
				.EPSG(DroneTrackingInformationOperatorDetailsDto.EPSGEnum._4326)
				.id("DE.HH-DJ-100")//TODO
				.locationType(DroneTrackingInformationOperatorDetailsDto.LocationTypeEnum.DYNAMIC)
				.location(point))
			.uasDetails(new DroneTrackingInformationUasDetailsDto()
				.identification(new DroneTrackingInformationUasDetailsIdentificationDto()
					.serialNumber(flightOperation.getDevicesn()))
				.type(DroneTrackingInformationUasDetailsDto.TypeEnum.HELICOPTER)
				.operationalStatus(DroneTrackingInformationUasDetailsDto.OperationalStatusEnum.AIRBOURNE)
			)
			.telemetry(new DroneTrackingInformationTelemetryDto()
				.EPSG(DroneTrackingInformationTelemetryDto.EPSGEnum._4326)
				.position(point)
				.altitude(new AltitudeDto()
					.reference(AltitudeDto.ReferenceEnum.valueOf("AMSL_EGM2008")) // Nach Topic Definition sollten die DJI Drohnen AMSL übertragen
					.value(droneState.getHeight()) //TODO: Hier vielleicht Korrekturwert fürs Testen addieren
					.units(AltitudeDto.UnitsEnum.M))
				.groundSpeed(2.0) //TODO
				.verticalSpeed(0.0)
				.flightDirection((double) flightDirection.orElse(0f)) //TODO
				.accuracyGroundSpeed(3)
				.accuracyHorizontalPosition(11)
				.accuracyVerticalPosition(5)
			);
	}

	public void publishDroneState(OsdSubDeviceReceiver droneState, FlightOperation flightOperation) {
		DroneTrackingInformationDto dto = toDto(droneState,flightOperation);
		try {
			trackingApi.noticeDroneStates(
					List.of(dto)
				)
				.block();
		} catch (WebClientResponseException e) {
			LOGGER.error(e.getResponseBodyAsString());
		}
	}
}
