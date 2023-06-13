package com.dji.sample.wayline.domain.value;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DroneType {

	//	https://developer.dji.com/doc/cloud-api-tutorial/en/feature-set/dji-wpml/common-element.html
	//	89 (M350 RTK),
	//		60 (M300 RTK),
	//		67(M30/M30T) ,
	//		77（M3E/M3T/M3M）

	//	when droneEnumValue is 67(M30/M30T):
	//		0(M30),
	//		1(M30T)
	//	when droneEnumValue is 77(M3E/M3T/M3M):
	//		0(M3E)
	//		1(M3T)
	//		2(M3M)

	M350_RTK("Matrice 350 RTK", 89, 0),
	M300_RTK("Matrice 300 RTK", 60, 0),
	M30("Matrice 30", 67, 0),
	M30_T("Matrice 30 T", 67, 1),
	M3_E("Matrice 3 E", 77, 0),
	M3_T("Matrice 3 T", 77, 1),
	M3_M("Matrice 3 M", 77, 2);

	private final String displayText;

	private final int code;

	private final int subcode;

	public static DroneType find(int code, int subcode) {
		return Arrays.stream(values())
			.filter(type -> type.code == code && type.subcode == subcode)
			.findAny()
			.orElseThrow(() -> new RuntimeException(
				"Could not find DroneType enum value for code " + code + " and subcode " + subcode));
	}

	@Override
	public String toString() {
		return displayText;
	}
}
