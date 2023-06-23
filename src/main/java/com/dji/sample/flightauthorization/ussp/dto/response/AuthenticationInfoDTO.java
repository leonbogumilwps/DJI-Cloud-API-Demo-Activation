package com.dji.sample.flightauthorization.ussp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationInfoDTO {
	private String fullName;
	private String email;
	private boolean isAdmin;
	private String jwtAuthToken;
}
