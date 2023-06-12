package com.dji.sample.flightauthorization.domain.value;

import static org.valid4j.Assertive.require;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class UserName {
	public static final int MAX_LENGTH = 255;

	@Getter
	@JsonValue
	@NonNull
	String value;

	private UserName(String value) {
		require(isValid(value));
		this.value = value;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static UserName of(String value) {
		return new UserName(value);
	}

	public static boolean isValid(String value) {
		return value.length() <= MAX_LENGTH;
	}

	@Override
	public String toString() {
		return value;
	}
}
