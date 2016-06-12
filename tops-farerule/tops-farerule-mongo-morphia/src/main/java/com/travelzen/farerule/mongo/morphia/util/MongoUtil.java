package com.travelzen.farerule.mongo.morphia.util;

import java.net.UnknownHostException;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.MongoClient;
import com.travelzen.farerule.mongo.morphia.config.MongoConfig;
import com.travelzen.mongo.morphia.DataStoreFactory;

public class MongoUtil {
	
	public static Datastore getDatastore() {
		String mongoUri = MongoConfig.getFareruleUri();
        DataStoreFactory dsFactory = new DataStoreFactory();
        dsFactory.init();
        Datastore ds = null;
        try {
            ds = dsFactory.createDatastore(mongoUri);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ds;
	}
	
	public static Datastore getFareDatastore() {
		String mongoUri = MongoConfig.getFareUri();
        DataStoreFactory dsFactory = new DataStoreFactory();
        dsFactory.init();
        Datastore ds = null;
        try {
            ds = dsFactory.createDatastore(mongoUri);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ds;
	}
	
	public static Datastore getDatastoreT() {
		Datastore datastore = null;
    	try {
    		Morphia morphia = new Morphia();
//    		MongoClient mongoClient = new MongoClient(MongoConfig.getUri());
//    		MongoClient mongoClient = new MongoClient("localhost", 27017);
    		MongoClient mongoClient = new MongoClient("192.168.161.189", 27017);
//    		MongoClient mongoClient = new MongoClient("mongodb://192.168.163.201:27017/");
        	datastore = morphia.createDatastore(mongoClient, "farerule");
        	datastore.ensureIndexes();
        	datastore.ensureCaps();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return datastore;
	}
}
