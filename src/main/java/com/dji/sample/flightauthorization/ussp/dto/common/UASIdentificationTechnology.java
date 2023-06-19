package com.dji.sample.flightauthorization.ussp.dto.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UASIdentificationTechnology {
	ADS_B("ADS-B"),
	FLARM("FLARM"),
	MODES("MODES"),
	WIFI("WIFI");

	private String value;

	@Override
	public String toString() {
		return value;
	}
}
