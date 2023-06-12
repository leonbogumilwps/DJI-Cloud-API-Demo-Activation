package com.dji.sample.flightauthorization.ussp.view;

import java.time.Instant;
import java.util.List;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;

import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlightAuthorizationRequestView {
	private String flightOperationId;
	private String correlationId;
	private String uasOperatorRegistrationNumber;
	private String title;
	private String description;
	private Instant takeOffTime;
	private Instant landingTime;
	private OperationalVolumeView operationalVolume;
	private ModeOfOperation modeOfOperation;
	private TypeOfFlight typeOfFlight;
	private List<UnmannedAircraftView> unmannedAircrafts;
	private MultiPoint safetyLandingPoints;
	private LineString flightPath;
	private AuthorizationStatusView status;
	private ActivationStatusView activationStatus;
}
