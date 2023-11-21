package com.dji.sample.flightauthorization.domain.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.dji.sample.flightauthorization.api.ussp.sender.ActivationProxy;
import com.dji.sample.flightauthorization.api.ussp.sender.DroneTrackingProxy;
import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.value.ActivationStatus;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;
import com.dji.sample.flightauthorization.ussp.exception.SubmissionFailedException;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;

import de.hhlasky.uassimulator.api.ussp.dto.ActivationRequestResponseDto;

public class FlightExecutionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlightExecutionService.class);

	private final ActivationProxy activationProxy;

	private final DroneTrackingProxy droneTrackingProxy;

	private final FlightOperationRepository flightOperationRepository;

	public FlightExecutionService(ActivationProxy activationProxy, DroneTrackingProxy droneTrackingProxy, FlightOperationRepository flightOperationRepository) {
		this.activationProxy = activationProxy;
		this.droneTrackingProxy = droneTrackingProxy;
		this.flightOperationRepository = flightOperationRepository;
	}

	public void activateFlight(USSPFlightOperationId flightOperationId) throws SubmissionFailedException {
		try{
			FlightOperation flightOperation = flightOperationRepository.findFlightOperationByUsspFlightOperationId(flightOperationId).orElseThrow(EntityNotFoundException::new);
			ActivationRequestResponseDto response = activationProxy.activateApprovalRequest(flightOperationId.toString());
			LOGGER.info("Activate Request for FlightOperation {} successful", response.getFlightOperationId());
			flightOperation.activate();
			flightOperationRepository.save(flightOperation);
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public void deactivateFlight(USSPFlightOperationId flightOperationId) throws SubmissionFailedException {
		try{
			FlightOperation flightOperation = flightOperationRepository.findFlightOperationByUsspFlightOperationId(flightOperationId).orElseThrow(EntityNotFoundException::new);
			ActivationRequestResponseDto response = activationProxy.deactivateApprovalRequest(flightOperationId.toString());
			LOGGER.info("Deactivate Request for FlightOperation {} successful", response.getFlightOperationId());
			flightOperation.deactivate();
			flightOperationRepository.save(flightOperation);
		}
		catch (Exception e){
			throw new SubmissionFailedException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public Optional<FlightOperation> getActivatedFlight(String devicesn){
		List<FlightOperation> activatedFlights = flightOperationRepository.findFlightOperationByDevicesnAndActivationStatus(devicesn, ActivationStatus.ACTIVATED);
		if(!activatedFlights.isEmpty()){
			return Optional.of(activatedFlights.get(0));
		}
		else return Optional.empty();
	}

	public void sendDroneTelemetryData(OsdSubDeviceReceiver osdData, String deviceSn) {
		this.getActivatedFlight(deviceSn).ifPresent(flightOperation -> droneTrackingProxy.publishDroneState(osdData, flightOperation));
	}

}
