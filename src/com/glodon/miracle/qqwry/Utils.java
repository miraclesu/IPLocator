package com.glodon.miracle.qqwry;

import java.io.UnsupportedEncodingException;

public class Utils {
	public static long ipToLong(String ip) {
		byte[] b = new byte[4];
		String[] ip_arr = ip.split("\\.");
		for (int i = ip_arr.length - 1; i > -1; i--) {
			b[i] = new Integer(ip_arr[i]).byteValue();
		}
		return (b[3] & 0xFFL) | ((b[2] << 8) & 0xFF00L) | ((b[1] << 16) & 0xFF0000L) | ((b[0] << 24) & 0xFF000000L);
	}
	
	public static String encode(byte[] b, String charset) {
		try {
			return new String(b, charset);
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}
}
