package com.dji.sample.flightauthorization.repository;

import org.springframework.data.repository.CrudRepository;

import com.dji.sample.flightauthorization.domain.entity.FlightAuthorization;

public interface FlightAuthorizationRepository extends CrudRepository<FlightAuthorization, Long> {
}
