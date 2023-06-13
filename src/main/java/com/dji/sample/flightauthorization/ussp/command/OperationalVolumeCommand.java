package com.dji.sample.flightauthorization.ussp.command;

import org.locationtech.jts.geom.Polygon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperationalVolumeCommand {
	private Polygon area;
	private double minHeightInMeter;
	private double maxHeightInMeter;
}
