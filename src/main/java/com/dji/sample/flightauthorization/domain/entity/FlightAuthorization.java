package com.dji.sample.flightauthorization.domain.entity;

import java.time.Instant;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ExecutionTimeframe;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPId;
import com.dji.sample.flightauthorization.domain.value.UserName;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;

import lombok.Getter;

@Entity
@Getter
public class FlightAuthorization extends AbstractEntity {

	private UserName username;

	private WorkspaceId workspaceId;

	private WaylineFileId waylineId;

	private Title title;

	private Description description;

	@Embedded
	private ExecutionTimeframe timeframe;

	private USSPId usspId;

	@Enumerated(EnumType.STRING)
	private ModeOfOperation modeOfOperation;
}
