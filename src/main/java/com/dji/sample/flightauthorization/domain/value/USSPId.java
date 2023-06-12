package com.dji.sample.flightauthorization.domain.value;

import static org.valid4j.Assertive.require;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class USSPId {
	public static final int MAX_LENGTH = 255;

	@Getter
	@JsonValue
	@NonNull
	String value;

	private USSPId(String value) {
		require(isValid(value));
		this.value = value;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static USSPId of(String value) {
		return new USSPId(value);
	}

	public static boolean isValid(String value) {
		return value.length() <= MAX_LENGTH;
	}

	@Override
	public String toString() {
		return value;
	}
}
