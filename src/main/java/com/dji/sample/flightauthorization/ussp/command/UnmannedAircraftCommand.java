package com.dji.sample.flightauthorization.ussp.command;

import com.dji.sample.flightauthorization.ussp.view.UASCategory;
import com.dji.sample.flightauthorization.ussp.view.UASIdentificationTechnology;
import com.dji.sample.flightauthorization.ussp.view.UAVClass;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnmannedAircraftCommand {
	private String applicableEmergencyForConnectivityLoss;
	private UASCategory category;
	private int enduranceInMinutes;
	private UASIdentificationTechnology identificationTechnology;
	private String serialnumber;
	private UAVClass uavClass;
	private String registrationNumber;
}
