package com.dji.sample.flightauthorization.ussp.dto.request;

import java.time.Instant;
import java.util.List;

import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.ussp.dto.common.GeofenceDto;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
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
	private String correlationId;
	private String title;
	private String description;
	private Instant takeOffTime;
	private Instant landingTime;
	private TypeOfFlight typeOfFlight;
	private ModeOfOperation flightMode;
	private UASOperatorDTO operator;
	private List<WaypointDTO> waypoints;
	private List<SafetyLandingPointDTO> safetyLandingPoints;
	private GeofenceDto geofence;
	private UAVDTO uav;
}
