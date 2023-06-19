package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.Title;

public class TitleUserType extends AbstractSimpleValueUserType<Title, String> {

	public TitleUserType(){
		super(Title.class, String.class);
	}

	@Override
	protected String toDatabaseValue(Title value) {
		return value.toString();
	}

	@Override
	protected Title fromDatabaseValue(String databaseValue) {
		return Title.of(databaseValue);
	}
}
