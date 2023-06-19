package com.dji.sample.flightauthorization.ussp.dto.response;

import java.time.Instant;

import org.locationtech.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConflictView {
	private String description;
	private Geometry conflictArea;
	private Instant conflictTimeFrom;
	private Instant conflictTimeTo;
}
