package com.dji.sample.flightauthorization.ussp.view;

import java.time.Instant;
import java.util.List;

import com.dji.sample.flightauthorization.domain.value.AuthorisationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationStatusView {
	private AuthorisationStatus authorisationStatus;
	private String message;
	private Instant receivedAt;
	private Instant rejectedAt;
	private Instant approvedAt;
	private Instant cancelledAt;
	private Instant withdrawnAt;
	private List<ConflictView> conflicts;
}
