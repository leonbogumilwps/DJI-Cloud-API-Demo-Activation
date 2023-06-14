package com.dji.sample.flightauthorization.api.command;

import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.ModeOfOperation;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightAuthorizationRequestCommand {

	@NotNull
	private String uasOperatorRegistrationNumber;
	@NotNull
	private Title title;
	private Description description;
	@NotNull
	private Instant takeoffTime;
	@NotNull
	private Instant landingTime;
	@NotNull
	private ModeOfOperation modeOfOperation;
	@NotNull
	@NotEmpty
	private String uasSerialNumber;
	@NotNull
	private String waylineId;
}
