package com.dji.sample.manage.model.common;

import org.springframework.context.ApplicationEvent;

public class TelemetryEvent extends ApplicationEvent {
	private final Object osdData;
	private final String deviceSn;

	public TelemetryEvent(Object source, Object osdData, String deviceSn) {
		super(source);
		this.osdData = osdData;
		this.deviceSn = deviceSn;
	}

	public Object getOsdData() {
		return osdData;
	}

	public String getDeviceSn() { return deviceSn; }
}