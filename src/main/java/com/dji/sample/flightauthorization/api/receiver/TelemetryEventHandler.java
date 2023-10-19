package com.dji.sample.flightauthorization.api.receiver;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.dji.sample.flightauthorization.applicationservice.FlightOperationApplicationService;
import com.dji.sample.manage.model.common.TelemetryEvent;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;

@Service
public class TelemetryEventHandler {

	final FlightOperationApplicationService flightOperationApplicationService;

	public TelemetryEventHandler(FlightOperationApplicationService flightOperationApplicationService) {
		this.flightOperationApplicationService = flightOperationApplicationService;
	}

	@EventListener
	public void handleTelemetryEvent(TelemetryEvent event) {
		Object osdData = event.getOsdData();
		String deviceSn = event.getDeviceSn();
		if(osdData instanceof OsdSubDeviceReceiver) {
			flightOperationApplicationService.sendDroneTelemetryData((OsdSubDeviceReceiver) osdData, deviceSn);
		}
	}
}

