package com.dji.sample.common.util;

public class Validators {

	public static final String URL_PATTERN = "^(https?://)(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.?[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.,~#?&/=]*)$";

	public static boolean isValidURL(String value) {
		return value != null && value.matches(URL_PATTERN);
	}
}
