package com.dji.sample.wayline.domain.value;

import org.locationtech.jts.geom.Polygon;

import lombok.Getter;

@Getter
public class OperationalVolume {
	private final Polygon area;
	private final double minHeightInMeter;
	private final double maxHeightInMeter;

	public OperationalVolume(Polygon area, double minHeightInMeter, double maxHeightInMeter) {
		this.area = area;
		this.minHeightInMeter = minHeightInMeter;
		this.maxHeightInMeter = maxHeightInMeter;
	}
}
