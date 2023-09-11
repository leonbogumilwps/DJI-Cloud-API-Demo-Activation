package com.dji.sample.flightauthorization.api.request;

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
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightOperationRequestDTO {

	//@NotNull
	private String title;
	private String description;
	//@NotNull
	private String takeofftime;
	//@NotNull
	private String landingtime;
	//@NotNull
	private String modeofoperation;
	//@NotNull
	//@NotEmpty
	private String uasserialnumber;
	//@NotNull
	private String waylineid;
}
