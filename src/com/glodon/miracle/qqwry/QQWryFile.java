package com.glodon.miracle.qqwry;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class QQWryFile {
	private final static String IP_FILE = "./qqwry.dat";
	private static final int IP_RECORD_LENGTH = 7;
	private RandomAccessFile ipFile = null;
	
	public RandomAccessFile getIpFile() {
		return ipFile;
	}

	private static QQWryFile instance = null;
	
	public QQWryFile() {
		try {
			ipFile = new RandomAccessFile(IP_FILE, "r");
		} catch (FileNotFoundException e) {
			System.out.println(IP_FILE + "文件没有找到");
		}
	}
	
	public synchronized static QQWryFile getInstance() {
		if (null == instance)
			instance = new QQWryFile();
		return instance;
	}

	public QQWryRecord find(String ip, RandomAccessFile ipFile) {
		long ipValue = Utils.ipToLong(ip);
		QQWryHeader header = new QQWryHeader(ipFile);
		long first = header.getIpBegin();
		int left = 0;
		int right = (int) ((header.getIpEnd() - first) / IP_RECORD_LENGTH);
		int middle = 0;
		QQWryIndex middleIndex = null;
		//二分查找
		while (left <= right) {
			middle = (int) ((left + right) / 2);
			middleIndex = new QQWryIndex(ipFile, first + middle * IP_RECORD_LENGTH);
			if (ipValue > middleIndex.getStartIp())
				left = middle + 1;
			else if (ipValue < middleIndex.getStartIp())
				right = middle - 1;
			else
				return new QQWryRecord(ipFile, middleIndex.getIpPos());
		}
		middleIndex = new QQWryIndex(ipFile, first + right * IP_RECORD_LENGTH);
		return new QQWryRecord(ipFile, middleIndex.getIpPos());
	}
	
	public static void main(String[] args) {
		String ip = "116.233.156.204";
//		byte[] ret = Utils.getIpByteArrayFromString(ip);
//		for (int i = 0; i < ret.length; i++) {
//			System.out.println(ret[i]);
//		}
		
		QQWryFile qqWryFile = new QQWryFile();
//		QQWryHeader header = new QQWryHeader(qqWryFile.getIpFile());
//		QQWryIndex index = new QQWryIndex(qqWryFile.getIpFile(), (header.getIpEnd() + header.getIpBegin()) / 2);
//		System.out.println((header.getIpEnd() + header.getIpBegin()) / 2);
//		System.out.println(index.getStartIp());
//		System.out.println(index.getIpPos());
		QQWryRecord record = qqWryFile.find(ip, qqWryFile.getIpFile());
		System.out.println(record.getCountry());
		System.out.println(record.getArea());
	}
}
