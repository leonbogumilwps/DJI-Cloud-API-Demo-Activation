package com.dji.sample.flightauthorization.ussp.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitFlightAuthorizationRequestCommand {
}
