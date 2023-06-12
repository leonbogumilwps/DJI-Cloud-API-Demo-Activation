package com.dji.sample.flightauthorization.api;

import static com.dji.sample.component.AuthInterceptor.TOKEN_CLAIM;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;

import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.wayline.model.dto.WaylineFileDTO;

public class FlightAuthorizationRequestGuard {

	public void getRequest(String workspaceId, Long id, HttpServletRequest request) {
		CustomClaim customClaim = (CustomClaim)request.getAttribute(TOKEN_CLAIM);
		if(workspaceId != customClaim.getWorkspaceId()) {
			throw new AccessDeniedException("You are not authorized for given workspace.");
		}
	}
}
