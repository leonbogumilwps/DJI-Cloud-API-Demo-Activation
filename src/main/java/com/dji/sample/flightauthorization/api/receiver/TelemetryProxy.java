package com.dji.sample.flightauthorization.api.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.dji.sample.flightauthorization.api.ussp.sender.AuthorizationProxy;
import com.dji.sample.manage.model.common.TelemetryEvent;

@Service
public class TelemetryProxy {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationProxy.class);

	@EventListener
	public void handleTelemetryEvent(TelemetryEvent event) {
		Object osdData = event.getOsdData();
		LOGGER.debug("Received telemetry data: {}", osdData);
	}
}

