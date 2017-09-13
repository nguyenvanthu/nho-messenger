package com.nho.server.security.impl;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.nho.server.security.AbstractSecurity;

public class AesSecurity extends AbstractSecurity{

	private static final String Algorithm ="AES";
	@Override
	public String encrypt(String message) {
		Key key = generateKey();
		try {
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptValue = cipher.doFinal(message.getBytes());
			String encryptMessage = Base64.encodeBase64String(encryptValue);
			return encryptMessage;
		} catch (Exception exception) {
			getLogger().debug("error when encrypt message");
			exception.printStackTrace();
			return null;
		} 
	}

	@Override
	public String decrypt(String cipherText) {
		Key key = generateKey();
		try{
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decordedValue = Base64.decodeBase64(cipherText);
			byte[] decryptValue = cipher.doFinal(decordedValue);
			return new String(decryptValue);
		}catch(Exception exception){
			getLogger().debug("error when decrypt message");
			exception.printStackTrace();
			return null;
		}
	}
	
	private Key generateKey(){
		Key key = new SecretKeySpec(getKey().getBytes(), Algorithm);
		return key;
	}

	@Override
	public AbstractSecurity newInstance() {
		return new AesSecurity();
	}

}
