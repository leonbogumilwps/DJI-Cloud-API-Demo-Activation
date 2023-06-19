package com.dji.sample.flightauthorization.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dji.sample.flightauthorization.domain.entity.FlightOperation;

public interface FlightOperationRepository extends CrudRepository<FlightOperation, Long> {

	List<FlightOperation> findAll();
}
