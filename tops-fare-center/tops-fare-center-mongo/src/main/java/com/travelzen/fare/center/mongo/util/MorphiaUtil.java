package com.travelzen.fare.center.mongo.util;

import java.util.Properties;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.travelzen.fare.center.common.util.PropsUtil;

/**
 * Morphia工具类
 * <p>
 * @author yiming.yan
 * @Date Oct 21, 2015
 */
public class MorphiaUtil {
	
	private static Properties properties;
	
	static {
		properties = PropsUtil.loadProps("properties/mongo.properties");
	}
	
	public static Datastore getDatastore() {
		MongoClient mongoClient = new MongoClient(getMongoClientURI());
		Morphia morphia = new Morphia();
		return morphia.createDatastore(mongoClient, getDbName());
	}
	
	private static MongoClientURI getMongoClientURI() {
		String host = PropsUtil.getString(properties, "mongo.host");
		int port = PropsUtil.getInt(properties, "mongo.port");
		return new MongoClientURI("mongodb://" + host + ":" + port);
	}
	
	private static String getDbName() {
		return PropsUtil.getString(properties, "mongo.dbname");
	}

}
