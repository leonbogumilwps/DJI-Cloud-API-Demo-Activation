package com.dji.sample.flightauthorization.ussp.view;

import java.time.Instant;
import java.util.List;

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
