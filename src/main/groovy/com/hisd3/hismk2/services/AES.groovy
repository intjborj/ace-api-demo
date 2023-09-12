package com.hisd3.hismk2.services

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AES {
	
	private static SecretKeySpec secretKey
	private static byte[] key
	private static String myKey = "hcp" //MQePXiae0ja1QCLV4wCF
	
	static void setKey(String myKey) {
		MessageDigest sha = null
		try {
			key = myKey.getBytes("UTF-8")
			sha = MessageDigest.getInstance("SHA-1")
			key = sha.digest(key)
			key = Arrays.copyOf(key, 16)
			secretKey = new SecretKeySpec(key, "AES")
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace()
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace()
		}
	}
	
	static String encrypt(String strToEncrypt) {
		// strToEncrypt
		
		//  strToEncrypt.bytes.encodeBase64().toString()
		try {
			
			setKey(myKey)
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
			cipher.init(Cipher.ENCRYPT_MODE, secretKey)
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")))
		}
		catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString())
		}
		return null
	}
	
	static String decrypt(String strToDecrypt) {
		
		//strToDecrypt.decodeBase64()
		
		try {
			setKey(myKey)
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
			cipher.init(Cipher.DECRYPT_MODE, secretKey)
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
		}
		catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString())
		}
		return null
	}
}
