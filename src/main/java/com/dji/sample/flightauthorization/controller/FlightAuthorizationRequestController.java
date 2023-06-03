package com.dji.sample.flightauthorization.controller;

import static com.dji.sample.component.AuthInterceptor.TOKEN_CLAIM;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dji.sample.common.model.CustomClaim;

@RestController
@RequestMapping("${url.flight-authorization-request.version}${url.flight-authorization-request.prefix}")
public class FlightAuthorizationRequestController {

	@GetMapping("{workspace_id}")
	public void getAllRequests(@PathVariable("workspace_id") String workspaceId) {
	}

	@GetMapping("{workspace_id}/{id}")
	public void getRequest(
		@PathVariable("workspace_id") String workspaceId,
		@PathVariable("id") Long id) {
	}

	@PostMapping("{workspace_id}/create")
	public void createRequest(
		@PathVariable("workspace_id") String workspaceId,
		HttpServletRequest request) {
	}

	@PutMapping("{workspace_id}/{id}/cancel")
	public void cancelRequest(
		@PathVariable("workspace_id") String workspaceId,
		@PathVariable("id") Long id) {
	}
}
