package com.glodon.miracle.storage;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoDB {
	private Mongo conn = null;
	private DB db = null;
	private DBCollection coll = null;
	
	public DBCollection getColl() {
		return coll;
	}

	public MongoDB() {
		try {
			conn = new Mongo();
			db = conn.getDB("IPLocator");
			coll = db.getCollection("ips");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public MongoDB(String host, int port, String dbName, String collName) {
		try {
			conn = new Mongo(host, port);
			db = conn.getDB(dbName);
			coll = db.getCollection(collName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}