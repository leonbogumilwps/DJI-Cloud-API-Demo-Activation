package com.dji.sample.flightauthorization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.dji.sample.flightauthorization.domain.entity.FlightOperation;
import com.dji.sample.flightauthorization.domain.value.ActivationStatus;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;

public interface FlightOperationRepository extends CrudRepository<FlightOperation, Long> {

	List<FlightOperation> findAll();

	Optional<FlightOperation> findFlightOperationByUsspFlightOperationId(USSPFlightOperationId flightOperationId);

	List<FlightOperation> findFlightOperationByActivationStatus(ActivationStatus activationStatus);

	List<FlightOperation> findFlightOperationByDevicesnAndActivationStatus(String deviceSn, ActivationStatus activationStatus);
}
