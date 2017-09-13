package com.nho.server.security;

import java.util.UUID;

import com.nhb.common.BaseLoggable;

public abstract class AbstractSecurity extends BaseLoggable implements Securityable {
	private static final String Key = "AIzaSyD4eQvPx8wUxF5EOQliSWnC1k8qKqtrqA";
	private static byte[] sharedvector = { 0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11 };

	public byte[] getSharedVector() {
		return sharedvector;
	}

	public String getKey() {
		return Key;
	}
	
	public String getNewKey(){
		return UUID.randomUUID().toString();
	}
	
	public abstract AbstractSecurity newInstance();
}
