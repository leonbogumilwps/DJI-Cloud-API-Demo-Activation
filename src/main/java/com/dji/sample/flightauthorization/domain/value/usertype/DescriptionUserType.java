package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.Description;

public class DescriptionUserType extends AbstractSimpleValueUserType<Description, String> {

	public DescriptionUserType(){
		super(Description.class, String.class);
	}

	@Override
	protected String toDatabaseValue(Description value) {
		return value.toString();
	}

	@Override
	protected Description fromDatabaseValue(String databaseValue) {
		return Description.of(databaseValue);
	}
}
