package com.dji.sample.flightauthorization.ussp.view;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnmannedAircraftView {
	private String applicableEmergencyForConnectivityLoss;
	private UASCategory category;
	private int enduranceInMinutes;
	private UASIdentificationTechnology identificationTechnology;
	private String serialnumber;
	private UAVClass uavClass;
	private String registrationNumber;

}
