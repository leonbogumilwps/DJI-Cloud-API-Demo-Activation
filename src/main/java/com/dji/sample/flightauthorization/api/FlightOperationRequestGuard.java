package com.dji.sample.flightauthorization.api;

import static com.dji.sample.component.AuthInterceptor.TOKEN_CLAIM;

import javax.servlet.http.HttpServletRequest;

import com.dji.sample.common.model.CustomClaim;

public class FlightOperationRequestGuard {

	public void getAllRequests(String workspaceId, HttpServletRequest request) {
		checkUserHasPermissionsForWorkspace(workspaceId, request);
	}

	public void getRequest(String workspaceId, Long id, HttpServletRequest request) {
		checkUserHasPermissionsForWorkspace(workspaceId, request);
	}

	public void createRequest(String workspaceId, HttpServletRequest request) {
		checkUserHasPermissionsForWorkspace(workspaceId, request);
	}

	private void checkUserHasPermissionsForWorkspace(String workspaceId, HttpServletRequest request) {
		CustomClaim customClaim = (CustomClaim) request.getAttribute(TOKEN_CLAIM);
		if (!workspaceId.equals(customClaim.getWorkspaceId())) {
			throw new RuntimeException("You are not authorized for given workspace.");
		}
	}
}
