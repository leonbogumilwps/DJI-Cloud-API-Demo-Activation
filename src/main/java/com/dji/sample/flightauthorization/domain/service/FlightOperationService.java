package com.dji.sample.flightauthorization.domain.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.repository.FlightOperationRepository;

public class FlightOperationService {

	private FlightOperationRepository repository;

	public FlightOperationService(FlightOperationRepository repository) {
		this.repository = repository;
	}

	public FlightOperation get(Long id) {
		return repository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	public List<FlightOperation> getAll() {
		return repository.findAll();
	}

	public FlightOperation save(FlightOperation flightOperation) {
		return repository.save(flightOperation);
	}
}
