package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.Name;

public class NameUserType extends AbstractSimpleValueUserType<Name, String> {

	public NameUserType() {
		super(Name.class, String.class);
	}

	@Override
	protected String toDatabaseValue(Name value) {
		return value.toString();
	}

	@Override
	protected Name fromDatabaseValue(String databaseValue) {
		return Name.of(databaseValue);
	}
}
