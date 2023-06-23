package com.dji.sample.flightauthorization.ussp.dto.request;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaypointDTO {
	private Point position;
	private double altitudeInMeters;
	private double cruisingSpeed;
}
