package com.dji.sample.flightauthorization.ussp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginDTO {
	private String email;
	private String password;
}
