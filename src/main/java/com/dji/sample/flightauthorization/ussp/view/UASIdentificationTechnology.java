package com.dji.sample.flightauthorization.ussp.view;

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
