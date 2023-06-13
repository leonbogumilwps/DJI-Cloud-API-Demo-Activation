package com.dji.sample.wayline.domain.value;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayloadSubType {

	//	https://developer.dji.com/doc/cloud-api-tutorial/en/feature-set/dji-wpml/common-element.html#wpml-payloadinfo
	//	ZENMUSE_P1:
	//		0(LENS_24mm),
	//		1(LENS_35mm),
	//		2(LENS_50mm)

	LENS_24MM("24mm Lens", 0),
	LENS_35MM("35mm Lens", 1),
	LENS_50MM("50mm Lens", 2);

	private final String displayText;

	private final int code;

	public static PayloadSubType find(int code) {
		return Arrays.stream(values())
			.filter(type -> type.code == code)
			.findAny()
			.orElseThrow(() -> new RuntimeException(
				"Could not find DroneType enum value for code " + code));
	}

	@Override
	public String toString() {
		return displayText;
	}
}
