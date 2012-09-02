package com.glodon.miracle.storage;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class MongoDB {
	private Mongo conn = null;
	private DB db = null;
	private DBCollection coll = null;
	
	private static MongoDB instance = null;
	
	public Mongo getConn() {
		return conn;
	}

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
	
	public synchronized static MongoDB getInstance(String host, int port, String dbName, String collName) {
		if (null == instance)
			instance = new MongoDB(host, port, dbName, collName);
		return instance;
	}
	
	public synchronized static MongoDB getInstance() {
		if (null == instance)
			instance = new MongoDB();
		return instance;
	}
	
	public static WriteResult insertToMongoDBByBatch(MongoDB mongo, List<DBObject> batchPush) {
		return mongo.getColl().insert(batchPush);
	}

	public void close(MongoDB mongo) {
		mongo.getConn().close();
		mongo = null;
	}
}