package com.ludovic.vimont;

public class Constants {
	public static final char[] BASE32 = { 
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
			'y', 'z' 
	};
	public static final byte[] BASE32_INV = new byte[(int) 'z' + 1];
	
	static {
		for (int i = 0; i < BASE32.length; i++) {
			BASE32_INV[(int) BASE32[i]] = (byte) i;
		}
	}
}