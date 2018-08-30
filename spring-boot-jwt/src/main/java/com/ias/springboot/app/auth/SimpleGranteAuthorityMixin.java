package com.ias.springboot.app.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGranteAuthorityMixin {

	@JsonCreator
	public SimpleGranteAuthorityMixin(@JsonProperty("authority") String role) {}

}
