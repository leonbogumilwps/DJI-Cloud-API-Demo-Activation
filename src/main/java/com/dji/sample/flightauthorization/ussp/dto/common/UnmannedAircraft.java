package com.dji.sample.flightauthorization.ussp.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnmannedAircraft {
	private String applicableEmergencyForConnectivityLoss;
	private UASCategory category;
	private int enduranceInMinutes;
	private UASIdentificationTechnology identificationTechnology;
	private String serialnumber;
	private UAVClass uavClass;
	private String registrationNumber;

}
