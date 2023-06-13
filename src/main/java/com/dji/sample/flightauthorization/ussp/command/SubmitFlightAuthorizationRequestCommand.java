package com.dji.sample.flightauthorization.ussp.command;

import java.time.Instant;
import java.util.List;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;

import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.ussp.view.TypeOfFlight;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitFlightAuthorizationRequestCommand {

	private String uasOperatorRegistrationNumber;

	private String title;

	private String description;

	private Instant takeOffTime;

	private Instant landingTime;

	private OperationalVolumeCommand operationalVolume;

	private ModeOfOperation modeOfOperation;

	private TypeOfFlight typeOfFlight;

	private List<UnmannedAircraftCommand> unmannedAircrafts;

	private String correlationId;

	private MultiPoint safetyLandingPoints;

	private LineString flightPath;
}
