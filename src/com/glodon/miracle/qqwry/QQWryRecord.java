package com.glodon.miracle.qqwry;

import java.io.IOException;
import java.io.RandomAccessFile;

public class QQWryRecord {
	private long startIP = 0L;
	private long endIP = 0L;
	private String country = null;
	private String area = null;
	
	private static final byte AREA_FOLLOWED = 0x01;
	private static final byte NO_AREA = 0x2;
	
	public long getStartIP() {
		return startIP;
	}
	public long getEndIP() {
		return endIP;
	}
	public String getCountry() {
		return country;
	}
	public String getArea() {
		return area;
	}
	
	public static String getAreaFromFile(RandomAccessFile ipFile, long pos) {
		String area = "未知地区";
		try {
			ipFile.seek(pos);
			switch (ipFile.readByte()) {
			case AREA_FOLLOWED:
			case NO_AREA:
				pos = QQWryIO.readLong3(ipFile, pos + 1);
				if (pos > 0)
					area = QQWryIO.readString(ipFile, pos);
				break;
			default:
				area = QQWryIO.readString(ipFile, pos);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return area;
	}
	
	public QQWryRecord(long startIP, long endIP) {
		this.startIP = startIP;
		this.endIP = endIP;
	}
	
	public QQWryRecord(RandomAccessFile ipFile, long startIP, long pos) {
		this.startIP = startIP;
		endIP = QQWryIO.readLong4(ipFile, pos);
		try {
			ipFile.seek(pos + 4);
			switch (ipFile.readByte()) {
			case AREA_FOLLOWED:
				pos = QQWryIO.readLong3(ipFile, pos + 5);
				ipFile.seek(pos);
				switch (ipFile.readByte()) {
				case NO_AREA:
					country = QQWryIO.readString(ipFile, QQWryIO.readLong3(ipFile, pos + 1));
					pos += 4;
					break;
				default:
					country = QQWryIO.readString(ipFile, pos);
					pos = ipFile.getFilePointer();
					break;
				}
				break;
			case NO_AREA:
				country = QQWryIO.readString(ipFile, QQWryIO.readLong3(ipFile, pos + 5));
				pos += 8;
				break;
			default:
				country = QQWryIO.readString(ipFile, pos + 4);
				pos = ipFile.getFilePointer();
				break;
			}
			area = getAreaFromFile(ipFile, pos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
