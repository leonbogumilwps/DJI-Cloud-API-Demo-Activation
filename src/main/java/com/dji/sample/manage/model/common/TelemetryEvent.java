package com.dji.sample.manage.model.common;

import org.springframework.context.ApplicationEvent;

public class TelemetryEvent extends ApplicationEvent {
	private final Object osdData;

	public TelemetryEvent(Object source, Object osdData) {
		super(source);
		this.osdData = osdData;
	}

	public Object getOsdData() {
		return osdData;
	}
}