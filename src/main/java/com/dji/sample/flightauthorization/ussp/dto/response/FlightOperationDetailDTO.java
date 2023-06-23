package com.dji.sample.flightauthorization.ussp.dto.response;

import java.time.Instant;
import java.util.List;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;

import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.ussp.dto.common.GeofenceDto;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.dto.common.UnmannedAircraft;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlightOperationDetailDTO {
	private String flightOperationId;
	private String correlationId;
	private String uasOperatorRegistrationNumber;
	private String title;
	private String description;
	private Instant takeOffTime;
	private Instant landingTime;
	private GeofenceDto geofence;
	private ModeOfOperation modeOfOperation;
	private TypeOfFlight typeOfFlight;
	private List<UnmannedAircraft> unmannedAircrafts;
	private MultiPoint safetyLandingPoints;
	private LineString flightPath;
	private AuthorizationStatusView status;
	private ActivationStatusView activationStatus;
}
