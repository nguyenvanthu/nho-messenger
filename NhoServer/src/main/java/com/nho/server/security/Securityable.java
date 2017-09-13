package com.nho.server.security;

public interface Securityable {
	public String encrypt(String message);
	public String decrypt(String cipherText);
}
