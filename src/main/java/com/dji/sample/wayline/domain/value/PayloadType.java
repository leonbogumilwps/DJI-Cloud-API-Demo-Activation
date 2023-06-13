package com.dji.sample.wayline.domain.value;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayloadType {

	//	https://developer.dji.com/doc/cloud-api-tutorial/en/feature-set/dji-wpml/common-element.html#wpml-payloadinfo
	//	42 (H20),
	//		43 (H20T),
	//		50 (P1),
	//		52 (M30),
	//		53 (M30T),
	//		61 (H20N),
	//		90742 (L1)
	//		66 (Mavic 3E Camera)
	//		67 (Mavic 3T Camera)
	//		68 (Mavic 3M Camera)
	//		65534 (PSDK Payload Device)

	H20("H20", 42),
	H20_T("H20T", 43),
	P1("Zenmuse P1", 50),
	M30("M30", 52),
	M30_T("M30T", 53),
	H20_N("H20N", 61),
	L1("L1", 90742),
	M3E_CAM("Mavic 3E Camera", 66),
	M3T_CAM("Mavic 3T Camera", 67),
	M3M_CAM("Mavic 3M Camera", 68),
	PSDK_PAYLOAD_DEVICE("PSDK Payload Device", 65534);

	private final String displayText;

	private final int code;

	public static PayloadType find(int code) {
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
