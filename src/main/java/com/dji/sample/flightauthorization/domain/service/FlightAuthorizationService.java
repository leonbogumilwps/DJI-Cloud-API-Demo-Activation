package com.dji.sample.flightauthorization.domain.service;

import javax.persistence.EntityNotFoundException;

import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;
import com.dji.sample.flightauthorization.repository.FlightAuthorizationRepository;

public class FlightAuthorizationService {

	private FlightAuthorizationRepository repository;

	public FlightAuthorizationService(FlightAuthorizationRepository repository) {
		this.repository = repository;
	}

	public FlightAuthorization get(Long id) {
		return repository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
}
