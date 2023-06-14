package com.dji.sample.flightauthorization.ussp.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class SubmissionFailedException extends Exception {

	@Getter
	private final HttpStatus status;

	public SubmissionFailedException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
