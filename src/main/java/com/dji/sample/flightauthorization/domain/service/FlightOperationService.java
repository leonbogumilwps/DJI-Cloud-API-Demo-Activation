package com.dji.sample.flightauthorization.domain.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.value.ActivationStatus;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;

public class FlightOperationService {

	private final FlightOperationRepository repository;

	public FlightOperationService(FlightOperationRepository repository) {
		this.repository = repository;
	}

	public FlightOperation get(Long id) {
		return repository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	public List<FlightOperation> getAll() {
		return repository.findAll();
	}

	public Optional<FlightOperation> getActivatedFlight(){
		List<FlightOperation> activatedFlights = repository.findFlightOperationByActivationStatus(ActivationStatus.ACTIVATED);
		if(!activatedFlights.isEmpty()){
			return Optional.of(activatedFlights.get(0));
		}
		else return Optional.empty();
	}

	public FlightOperation save(FlightOperation flightOperation) {
		return repository.save(flightOperation);
	}

	public FlightOperation getByFlightOperationId(USSPFlightOperationId flightOperationId){
		return repository.findFlightOperationByUsspFlightOperationId(flightOperationId).orElseThrow(EntityNotFoundException::new);
	}
}
