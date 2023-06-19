package com.dji.sample.wayline.domain.entity;

import org.locationtech.jts.geom.LineString;

import com.dji.sample.wayline.domain.value.DroneType;
import com.dji.sample.wayline.domain.value.PayloadSubType;
import com.dji.sample.wayline.domain.value.PayloadType;
import com.dji.sample.wayline.model.enums.WaylineTemplateTypeEnum;

import lombok.Getter;

@Getter
public class Wayline {

	private final LineString flightPath;

	private final WaylineTemplateTypeEnum templateType;

	private final DroneType droneType;

	private final PayloadType payloadType;

	private final PayloadSubType payloadSubType;

	public Wayline(
		LineString flightPath,
		WaylineTemplateTypeEnum templateType,
		DroneType droneType,
		PayloadType payloadType,
		PayloadSubType payloadSubType) {
		this.flightPath = flightPath;
		this.templateType = templateType;
		this.droneType = droneType;
		this.payloadType = payloadType;
		this.payloadSubType = payloadSubType;
	}
}
