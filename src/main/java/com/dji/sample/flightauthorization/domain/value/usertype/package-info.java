@TypeDefs({
	@TypeDef(defaultForType = Title.class, typeClass = TitleUserType.class),
	@TypeDef(defaultForType = Description.class, typeClass = DescriptionUserType.class),
	@TypeDef(defaultForType = Name.class, typeClass = NameUserType.class),
	@TypeDef(defaultForType = WaylineFileId.class, typeClass = WaylineFileIdUserType.class),
	@TypeDef(defaultForType = USSPFlightOperationId.class, typeClass = USSPFlightOperationIdUserType.class),
	@TypeDef(defaultForType = WorkspaceId.class, typeClass = WorkspaceIdUserType.class)
})
package com.dji.sample.flightauthorization.domain.value.usertype;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.dji.sample.flightauthorization.domain.value.Description;
import com.dji.sample.flightauthorization.domain.value.Name;
import com.dji.sample.flightauthorization.domain.value.Title;
import com.dji.sample.flightauthorization.domain.value.USSPFlightOperationId;
import com.dji.sample.flightauthorization.domain.value.WaylineFileId;
import com.dji.sample.flightauthorization.domain.value.WorkspaceId;