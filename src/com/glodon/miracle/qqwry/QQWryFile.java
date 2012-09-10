package com.glodon.miracle.qqwry;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.glodon.miracle.storage.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class QQWryFile {
	private final static String IP_FILE = getQQWryFilePath();
	private static final int IP_RECORD_LENGTH = 7;
	private RandomAccessFile ipFile = null;

	public RandomAccessFile getIpFile() {
		return ipFile;
	}

	private static QQWryFile instance = null;
	
	public static String getQQWryFilePath() {
		try {
			return QQWryFile.class.getClassLoader().getResource("qqwry.dat").getPath();
		} catch (Exception e) {
			System.out.println("没有找到qqwry.dat文件");
			e.printStackTrace();
			return null;
		}
	}

	public QQWryFile() {
		try {
			if(null == IP_FILE)
				System.exit(1);
			ipFile = new RandomAccessFile(IP_FILE, "r");
		} catch (IOException e) {
			System.out.println("无法打开" + IP_FILE + "文件");
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
				return new QQWryRecord(ipFile, middleIndex.getStartIp(), middleIndex.getIpPos());
		}
		// 找不到精确的，取在范围内的
		middleIndex = new QQWryIndex(ipFile, first + right * IP_RECORD_LENGTH);
		QQWryRecord record = new QQWryRecord(ipFile, middleIndex.getStartIp(), middleIndex.getIpPos());
		if (ipValue >= record.getBeginIP() && ipValue <= record.getEndIP()) {
			return record;
		} else {
			//找不到相应的记录
			return new QQWryRecord(0L, ipValue);
		}
	}

	public void storageToMongoDB(RandomAccessFile ipFile, int batch) {
		MongoDB mongo = MongoDB.getInstance();
		List<DBObject> batchPush = new ArrayList<DBObject>(batch);
		QQWryHeader header = new QQWryHeader(ipFile);
		QQWryIndex index = null;
		QQWryRecord record = null;
		DBObject doc = null;
		int count = 0;
		long pos = header.getIpBegin();
		while (pos <= header.getIpEnd()) {
			index = new QQWryIndex(ipFile, pos);
			record = new QQWryRecord(ipFile, 0L, index.getIpPos());
			
			doc = new BasicDBObject();
			doc.put("ip_start", Utils.ipToStr(record.getBeginIP()));
			doc.put("ip_end", Utils.ipToStr(record.getEndIP()));
			doc.put("loc", record.getCountry());
			doc.put("isp", record.getArea());
			batchPush.add(doc);
			doc = null;
			
			if (count < batch)
				count++;
			else {
				MongoDB.insertToMongoDBByBatch(mongo, batchPush);
				batchPush.clear();
				count = 0;
			}
			
			pos += IP_RECORD_LENGTH;
		}
		mongo.close(mongo);
		batchPush = null;
		record = null;
		index = null;
		header = null;
	}

	public static void main(String[] args) {
		String ip = "202.108.22.5";
//		ip = "202.108.9.155";

		QQWryFile qqWryFile = QQWryFile.getInstance();
		RandomAccessFile ipFile = qqWryFile.getIpFile();
		QQWryRecord record = qqWryFile.find(ip, ipFile);
		System.out.println(Utils.ipToStr(record.getBeginIP()));
		System.out.println(Utils.ipToStr(record.getEndIP()));
		System.out.println(record.getCountry());
		System.out.println(record.getArea());
//		String path = QQWryFile.class.getClass().getResource("/qqwry.dat").getPath();
//		System.out.println(path);
//		System.out.println(Utils.ipToStr(3396081663L));
//		qqWryFile.storageToMongoDB(ipFile, 100);
		qqWryFile.closeIpFile(ipFile);
		qqWryFile = null;
	}
}
