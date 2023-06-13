package com.dji.sample.flightauthorization.domain.entity;

import java.time.Instant;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ExecutionTimeframe;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPId;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;
import com.dji.sample.flightauthorization.domain.value.ActivationStatus;
import com.dji.sample.flightauthorization.domain.value.AuthorisationStatus;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class FlightAuthorization extends AbstractEntity {

	private Name username;

	private WorkspaceId workspaceId;

	private WaylineFileId waylineId;

	private Title title;

	private Description description;

	@Embedded
	private ExecutionTimeframe timeframe;

	@Setter
	private USSPId usspId;

	@Enumerated(EnumType.STRING)
	private ModeOfOperation modeOfOperation;

	@Enumerated(EnumType.STRING)
	@Setter
	private ActivationStatus activationStatus;

	@Enumerated(EnumType.STRING)
	@Setter
	private AuthorisationStatus authorisationStatus;

	public FlightAuthorization(
		Name username,
		WorkspaceId workspaceId,
		WaylineFileId waylineId,
		Title title,
		Description description,
		Instant takeOffTime,
		Instant landingTime,
		ModeOfOperation modeOfOperation) {
		this.username = username;
		this.workspaceId = workspaceId;
		this.waylineId = waylineId;
		this.title = title;
		this.description = description;
		this.timeframe = new ExecutionTimeframe(takeOffTime, landingTime);
		this.modeOfOperation = modeOfOperation;
	}
}
