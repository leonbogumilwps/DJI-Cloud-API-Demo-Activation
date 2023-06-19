package com.dji.sample.flightauthorization.ussp.dto.request;

import java.time.Instant;
import java.util.List;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;

import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.ussp.dto.common.OperationalVolume;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.dto.common.UnmannedAircraft;
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
public class SubmitFlightAuthorizationRequestDTO {

	private String uasOperatorRegistrationNumber;

	private String title;

	private String description;

	private Instant takeOffTime;

	private Instant landingTime;

	private OperationalVolume operationalVolume;

	private ModeOfOperation modeOfOperation;

	private TypeOfFlight typeOfFlight;

	private List<UnmannedAircraft> unmannedAircrafts;

	private String correlationId;

	private MultiPoint safetyLandingPoints;

	private LineString flightPath;
}
