package com.travelzen.farerule.mongo.morphia.config;

import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.farerule.mongo.morphia.consts.MongoConst;
import com.travelzen.farerule.mongo.morphia.consts.PathConst;

public class MongoConfig {

    private static String fareruleUri;
    private static String fareUri;

    static {
    	fareruleUri = TopsConfReader.getConfContent(
        		PathConst.MONGO_CONFIG_FILE, MongoConst.FARERULE_MONGO_URI, ConfScope.R);
    	fareUri = TopsConfReader.getConfContent(
        		PathConst.MONGO_CONFIG_FILE, MongoConst.FARE_MONGO_URI, ConfScope.R);
    }

    public static String getFareruleUri() {
        return fareruleUri;
    }
    
    public static String getFareUri() {
        return fareUri;
    }
}
