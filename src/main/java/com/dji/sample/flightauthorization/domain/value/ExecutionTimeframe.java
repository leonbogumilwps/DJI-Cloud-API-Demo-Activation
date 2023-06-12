package com.dji.sample.flightauthorization.domain.value;

import static org.valid4j.Assertive.require;

import java.time.Instant;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Embeddable
public class ExecutionTimeframe {

	private Instant takeoffTime;

	private Instant landingTime;

	private ExecutionTimeframe(Instant takeoffTime, Instant landingTime) {
		this.takeoffTime = takeoffTime;
		this.landingTime = landingTime;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static ExecutionTimeframe of(Instant takeoffTime, Instant landingTime) {
		return new ExecutionTimeframe(takeoffTime, landingTime);
	}
}

