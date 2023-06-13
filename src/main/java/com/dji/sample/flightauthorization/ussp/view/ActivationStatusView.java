package com.dji.sample.flightauthorization.ussp.view;

import java.time.Instant;

import com.dji.sample.flightauthorization.domain.value.ActivationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivationStatusView {
	private ActivationStatus activationStatus;
	private String message;
	private Instant receivedAt;
	private Instant activatedAt;
	private Instant rejectedAt;
	private Instant deactivatedAt;
}
