package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;

public class USSPFlightOperationIdUserType extends AbstractSimpleValueUserType<USSPFlightOperationId, String> {

	public USSPFlightOperationIdUserType() {
		super(USSPFlightOperationId.class, String.class);
	}

	@Override
	protected String toDatabaseValue(USSPFlightOperationId value) {
		return value.toString();
	}

	@Override
	protected USSPFlightOperationId fromDatabaseValue(String databaseValue) {
		return USSPFlightOperationId.of(databaseValue);
	}
}
