package com.glodon.miracle.qqwry;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <pre>
 * qqwry.dat文件的格式是:   
 * 一. 文件头，共8字节   
 *    1. 第一个起始IP的绝对偏移， 4字节   
 *     2. 最后一个起始IP的绝对偏移， 4字节   
 * 二. &quot;结束地址/国家/区域&quot;记录区   
 *     四字节ip地址后跟的每一条记录分成两个部分   
 *     1. 国家记录   
 *     2. 地区记录   
 *     但是地区记录是不一定有的。而且国家记录和地区记录都有两种形式   
 *     1. 以0结束的字符串   
 *     2. 4个字节，一个字节可能为0x1或0x2   
 *   a. 为0x1时，表示在绝对偏移后还跟着一个区域的记录，注意是绝对偏移之后，而不是这四个字节之后   
 *        b. 为0x2时，表示在绝对偏移后没有区域记录   
 *        不管为0x1还是0x2，后三个字节都是实际国家名的文件内绝对偏移   
 *   如果是地区记录，0x1和0x2的含义不明，但是如果出现这两个字节，也肯定是跟着3个字节偏移，如果不是   
 *        则为0结尾字符串   
 * 三. &quot;起始地址/结束地址偏移&quot;记录区   
 *     1. 每条记录7字节，按照起始地址从小到大排列   
 *        a. 起始IP地址，4字节   
 *        b. 结束ip地址的绝对偏移，3字节   
 *    
 * 注意，这个文件里的ip地址和所有的偏移量均采用little-endian格式，而java是采用   
 * big-endian格式的，要注意转换
 * 
 * 更详细说明请参考：http://lumaqq.linuxsir.org/article/qqwry_format_detail.html
 * </pre>
 * 
 */
public class QQWryRecord {
	private long beginIP = 0L;
	private long endIP = 0L;
	private String country = "未知国家";
	private String area = "未知地区";
	
	private static final byte AREA_FOLLOWED = 0x01;
	private static final byte NO_AREA = 0x2;
	
	public long getBeginIP() {
		return beginIP;
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
		this.beginIP = startIP;
		this.endIP = endIP;
	}
	
	public QQWryRecord(RandomAccessFile ipFile, long startIP, long pos) {
		this.beginIP = startIP;
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
