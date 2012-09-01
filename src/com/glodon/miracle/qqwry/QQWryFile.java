package com.glodon.miracle.qqwry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.glodon.miracle.storage.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

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

	public void closeIpFile(RandomAccessFile ipFile) {
		try {
			ipFile.close();
			ipFile = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != ipFile)
				ipFile = null;
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
		// 二分查找
		while (left <= right) {
			// 无符号右移，防止溢出
			middle = (left + right) >>> 1;
			middleIndex = new QQWryIndex(ipFile, first + middle
					* IP_RECORD_LENGTH);
			if (ipValue > middleIndex.getStartIp())
				left = middle + 1;
			else if (ipValue < middleIndex.getStartIp())
				right = middle - 1;
			else
				return new QQWryRecord(ipFile, middleIndex.getIpPos());
		}
		// 找不到精确的，取一个最相近的
		middleIndex = new QQWryIndex(ipFile, first + right * IP_RECORD_LENGTH);
		return new QQWryRecord(ipFile, middleIndex.getIpPos());
	}

	public void storageToMongoDBByBatch(RandomAccessFile ipFile,
			QQWryRecord[] records) {
		MongoDB mongo = new MongoDB();
		DBCollection coll = mongo.getColl();
		List<DBObject> batchPush = new ArrayList<DBObject>();

		for (int i = 0; i < records.length; i++) {
			DBObject doc = new BasicDBObject();
			doc.put("ip", records[i].getIp());
			doc.put("loc", records[i].getCountry());
			doc.put("isp", records[i].getArea());
			batchPush.add(doc);
			doc = null;
		}
		
		coll.insert(batchPush);
	}

	public static void main(String[] args) {
		String ip = "202.108.22.5";

		QQWryFile qqWryFile = new QQWryFile();
		RandomAccessFile ipFile = qqWryFile.getIpFile();
		QQWryRecord record = qqWryFile.find(ip, ipFile);
		System.out.println(record.getCountry());
		System.out.println(record.getArea());
		qqWryFile.closeIpFile(ipFile);
		qqWryFile = null;
	}
}
