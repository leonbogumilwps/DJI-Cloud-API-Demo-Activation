package com.dji.sample.flightauthorization.api;

import static com.dji.sample.component.AuthInterceptor.TOKEN_CLAIM;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.flightauthorization.api.request.CreateFlightOperationRequestDTO;
import com.dji.sample.flightauthorization.api.response.FlightOperationListDTO;
import com.dji.sample.flightauthorization.applicationservice.FlightOperationApplicationService;
import com.dji.sample.flightauthorization.ussp.dto.response.FlightOperationDetailDTO;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;

import lombok.NonNull;

@RestController
@RequestMapping("${url.flight-authorization-request.version}${url.flight-authorization-request.prefix}")
public class FlightOperationRequestController {

	private final FlightOperationRequestGuard guard;
	private final FlightOperationApplicationService applicationService;

	public FlightOperationRequestController(
		@NonNull FlightOperationRequestGuard flightOperationRequestGuard,
		@NonNull FlightOperationApplicationService flightOperationApplicationService) {
		this.guard = flightOperationRequestGuard;
		this.applicationService = flightOperationApplicationService;
	}

	@GetMapping("{workspace_id}")
	public List<FlightOperationListDTO> getAllRequests(
		@PathVariable("workspace_id") String workspaceId,
		HttpServletRequest request) {
		guard.getAllRequests(workspaceId, request);
		return applicationService.getAllRequests();
	}

	@GetMapping("{workspace_id}/{id}")
	public ResponseEntity<FlightOperationDetailDTO> getRequest(
		@PathVariable("workspace_id") String workspaceId,
		@PathVariable("id") Long id,
		HttpServletRequest request) {
		guard.getRequest(workspaceId, id, request);
		return applicationService.getRequest(id);
	}

	@PostMapping("{workspace_id}/create")
	public ResponseEntity createRequest(
		@PathVariable("workspace_id") String workspaceId,
		@RequestBody @Valid CreateFlightOperationRequestDTO requestDto,
		HttpServletRequest request) {
		guard.createRequest(workspaceId, request);
		CustomClaim customClaim = (CustomClaim) request.getAttribute(TOKEN_CLAIM);
		try {
			return ResponseEntity
				.ok(applicationService.submitRequest(workspaceId, customClaim.getUsername(), requestDto));
		} catch (SubmissionFailedException e) {
			return ResponseEntity
				.status(e.getStatus())
				.body(e.getMessage());
		}
	}

}
