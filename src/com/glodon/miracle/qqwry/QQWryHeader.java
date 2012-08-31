package com.glodon.miracle.qqwry;

import java.io.IOException;
import java.io.RandomAccessFile;

public class QQWryHeader {
	private long ipBegin = 0L;
	private long ipEnd = 0L;

	public long getIpBegin() {
		return ipBegin;
	}

	public long getIpEnd() {
		return ipEnd;
	}

	public QQWryHeader(RandomAccessFile ipFile) {
		ipBegin = QQWryIO.readLong4(ipFile, 0);
		ipEnd = QQWryIO.readLong4(ipFile, 4);
		if (-1 == ipBegin || -1 == ipEnd) {
			System.out.println("IP地址信息文件格式有错误");
			try {
				ipFile.close();
			} catch (IOException e) {
				ipFile = null;
			}
			System.exit(1);
		}
	}

}
