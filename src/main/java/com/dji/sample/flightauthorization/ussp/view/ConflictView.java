package com.dji.sample.flightauthorization.ussp.view;

import java.time.Instant;

import org.locationtech.jts.geom.Geometry;

public class ConflictView {
	private String description;
	private Geometry conflictArea;
	private Instant conflictTimeFrom;
	private Instant conflictTimeTo;
}
