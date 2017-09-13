package com.nho.server.security.impl;

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.nho.server.security.AbstractSecurity;

public class MD5Security extends AbstractSecurity {

	private static final String ALGORITHM ="MD5";
	private static final String CIPHER ="DESede/CBC/PKCS5Padding";
	@Override
	public String encrypt(String message) {
		String result ;
		byte[] keyArray = new byte[24];
		byte[] temporaryKey ;
		byte[] toEncryptArray = null;
		try{
			toEncryptArray = message.getBytes("UTF-8");
			MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
			temporaryKey = messageDigest.digest(getKey().getBytes("UTF-8"));
			if(temporaryKey.length<24){
				int index = 0;
				for(int i=temporaryKey.length;i<24;i++){
					keyArray[i] = temporaryKey[index];
				}
			}
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyArray	, "DESede"),new IvParameterSpec(getSharedVector()));
			byte[] encrypted = cipher.doFinal(toEncryptArray);
			result = Base64.encodeBase64String(encrypted);
			return result;
		}catch(Exception exception){
			getLogger().debug("error when encrypt data "+exception);
			return null;
		}
		
	}

	@Override
	public String decrypt(String cipherText) {
		byte[] keyArray = new byte[24];
        byte[] temporaryKey;
        String result ;
        try{
        	MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            temporaryKey = messageDigest.digest(getKey().getBytes("UTF-8"));           
 
            if(temporaryKey.length < 24) // DESede require 24 byte length key
            {
                int index = 0;
                for(int i=temporaryKey.length;i< 24;i++)
                {                  
                    keyArray[i] =  temporaryKey[index];
                }
            }
            
            Cipher c = Cipher.getInstance(CIPHER);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(getSharedVector()));
            byte[] decrypted = c.doFinal(Base64.decodeBase64(cipherText));   
 
            result = new String(decrypted, "UTF-8");   
            return result;
        }catch(Exception exception){
        	getLogger().debug("error when decrypt data "+exception);
        	return null;
        }
		
	}

	@Override
	public AbstractSecurity newInstance() {
		return new MD5Security();
	}

}
