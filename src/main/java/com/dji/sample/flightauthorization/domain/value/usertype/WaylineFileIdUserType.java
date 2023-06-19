package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;

public class WaylineFileIdUserType extends AbstractSimpleValueUserType<WaylineFileId, String> {

	public WaylineFileIdUserType() {
		super(WaylineFileId.class, String.class);
	}

	@Override
	protected String toDatabaseValue(WaylineFileId value) {
		return value.toString();
	}

	@Override
	protected WaylineFileId fromDatabaseValue(String databaseValue) {
		return WaylineFileId.of(databaseValue);
	}
}
