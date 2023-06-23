package com.dji.sample.flightauthorization.ussp.dto.response;

import java.time.Instant;

import com.dji.sample.flightauthorization.domain.value.ApprovalRequestStatus;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.ussp.dto.common.TypeOfFlight;
import com.dji.sample.flightauthorization.ussp.dto.common.UASOperatorDTO;
import com.dji.sample.flightauthorization.ussp.dto.common.UAVDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlightOperationDetailDTO {
	private long flightId;
	private String flightOperationId;
	private String correlationId;
	private UASOperatorDTO operatorDTO;
	private String title;
	private String description;
	private TypeOfFlight typeOfFlight;
	private ModeOfOperation flightMode;
	private Instant takeOffTime;
	private Instant landingTime;
	private String flightDuration;
	private UAVDTO uavDTO;
	private String createdAt;
	private ApprovalRequestStatus status;
	private String rejectionReason;
	private String rejectedAt;
	private EditorDTO editorDTO;
	private double flightDistanceInMeters;
}
