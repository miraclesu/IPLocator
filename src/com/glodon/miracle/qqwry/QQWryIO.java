package com.glodon.miracle.qqwry;

import java.io.IOException;
import java.io.RandomAccessFile;

public class QQWryIO {
	
	public static long readLong4(RandomAccessFile ipFile, long pos) {
		try {
			byte[] b = new byte[4];
			ipFile.seek(pos);
			ipFile.read(b, 0, 4);
			return (b[0] & 0xFFL) | ((b[1] << 8) & 0xFF00L) | ((b[2] << 16) & 0xFF0000L) | ((b[3] << 24) & 0xFF000000L);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static long readLong3(RandomAccessFile ipFile, long pos) {
		try {
			byte[] b = new byte[3];
			ipFile.seek(pos);
			ipFile.read(b, 0, 3);
			return (b[0] & 0xFFL) | ((b[1] << 8) & 0xFF00L) | ((b[2] << 16) & 0xFF0000L);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static String readString(RandomAccessFile ipFile, long pos) {
		try {
			byte[] b = new byte[255];
			ipFile.seek(pos);
			int i = 0;
			for (b[i] = ipFile.readByte(); b[i] != 0 ; b[++i] = ipFile.readByte());
			return Utils.encode(b, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
