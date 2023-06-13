package com.dji.sample.flightauthorization.api.view;

import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ExecutionTimeframe;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.ussp.view.ActivationStatus;

import lombok.Getter;

@Getter
public class FlightAuthorizationListView {

	private final Long id;
	private final Name username;
	private final Title title;
	private final Description description;
	private final ExecutionTimeframe timeframe;
	private final ModeOfOperation modeOfOperation;
	private final ActivationStatus activationStatus;

	public FlightAuthorizationListView(FlightAuthorization authorization) {
		this.id = authorization.getId();
		this.username = authorization.getUsername();
		this.title = authorization.getTitle();
		this.description = authorization.getDescription();
		this.timeframe = authorization.getTimeframe();
		this.modeOfOperation = authorization.getModeOfOperation();
		this.activationStatus = authorization.getActivationStatus();
	}
}
