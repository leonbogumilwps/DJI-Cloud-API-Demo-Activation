package com.dji.sample.flightauthorization.domain.entity;

import java.time.Instant;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.dji.sample.flightauthorization.domain.value.ActivationStatus;
import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ExecutionTimeframe;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;

import de.hhlasky.uassimulator.api.ussp.dto.AuthorisationStatusDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FlightOperation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private Long id;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.NameUserType")
	private Name username;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.WorkspaceIdUserType")
	private WorkspaceId workspaceId;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.WaylineFileIdUserType")
	private WaylineFileId waylineId;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.TitleUserType")
	private Title title;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.DescriptionUserType")
	private Description description;

	@Embedded
	private ExecutionTimeframe timeframe;

	@Type(type = "com.dji.sample.flightauthorization.domain.value.usertype.USSPFlightOperationIdUserType")
	private USSPFlightOperationId usspFlightOperationId;

	@Enumerated(EnumType.STRING)
	private ModeOfOperation modeOfOperation;

	@Enumerated(EnumType.STRING)
	@Setter
	private ActivationStatus activationStatus;

	@Enumerated(EnumType.STRING)
	@Setter
	private AuthorisationStatusDto.AuthorisationStatusEnum authorisationStatus;

	public FlightOperation(
		Name username,
		WorkspaceId workspaceId,
		WaylineFileId waylineId,
		Title title,
		Description description,
		Instant takeOffTime,
		Instant landingTime,
		ModeOfOperation modeOfOperation,
		USSPFlightOperationId usspFlightOperationId) {
		this.username = username;
		this.workspaceId = workspaceId;
		this.waylineId = waylineId;
		this.title = title;
		this.description = description;
		this.timeframe = new ExecutionTimeframe(takeOffTime, landingTime);
		this.modeOfOperation = modeOfOperation;
		this.usspFlightOperationId = usspFlightOperationId;
		this.activationStatus = ActivationStatus.NOT_ACTIVATED;
		this.authorisationStatus = AuthorisationStatusDto.AuthorisationStatusEnum.WITHDRAWN; //TODO
	}
}
