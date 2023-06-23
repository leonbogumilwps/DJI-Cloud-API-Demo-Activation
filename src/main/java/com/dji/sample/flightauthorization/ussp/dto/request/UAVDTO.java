package com.dji.sample.flightauthorization.ussp.dto.request;

import com.dji.sample.flightauthorization.ussp.dto.common.UASCategory;
import com.dji.sample.flightauthorization.ussp.dto.common.UASIdentificationTechnology;
import com.dji.sample.flightauthorization.ussp.dto.common.UAVClass;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UAVDTO {
	private String serialnumber;
	private String registrationId;
	private UASCategory category;
	private UAVClass uavClass;
	private UASIdentificationTechnology identificationTechnology;
	private boolean expectedConnectivityMethod;
	private int endurance;
	private String applicableEmergencyForConnectivityLoss;
}
