package com.dji.sample.flightauthorization.domain.value.usertype;

import com.dji.sample.common.domain.value.usertype.AbstractSimpleValueUserType;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;

public class WorkspaceIdUserType extends AbstractSimpleValueUserType<WorkspaceId, String> {

	public WorkspaceIdUserType() {
		super(WorkspaceId.class, String.class);
	}

	@Override
	protected String toDatabaseValue(WorkspaceId value) {
		return value.toString();
	}

	@Override
	protected WorkspaceId fromDatabaseValue(String databaseValue) {
		return WorkspaceId.of(databaseValue);
	}
}
