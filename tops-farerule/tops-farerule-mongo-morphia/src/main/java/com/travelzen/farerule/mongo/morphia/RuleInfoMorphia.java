package com.travelzen.farerule.mongo.morphia;

import java.util.List;

import com.github.jmkgreen.morphia.Datastore;
import com.travelzen.fare.RuleInfo;
import com.travelzen.farerule.mongo.morphia.util.MongoUtil;

public enum RuleInfoMorphia {

	Instance;
	
	private static Datastore datastore;

	static {
    	datastore = MongoUtil.getFareDatastore();
	}

	public long count() {
		return datastore.createQuery(RuleInfo.class).countAll();
	}
	
	public void deleteByRuleInfoId(String id) {
		datastore.delete(datastore.createQuery(RuleInfo.class).filter("id =", id));
	}
	
	public void deleteByOriginalRuleId(String id) {
		datastore.delete(datastore.createQuery(RuleInfo.class).filter("originalRuleInfoId =", id));
	}

	public void deleteAll() {
		datastore.delete(datastore.createQuery(RuleInfo.class));
	}

	public RuleInfo findByRuleInfoId(String id) {
		RuleInfo ruleInfo = datastore.find(RuleInfo.class, "id =", id).get();
		return ruleInfo;
	}
	
	public RuleInfo findByOriginalRuleId(String id) {
		RuleInfo ruleInfo = datastore.find(RuleInfo.class, "originalRuleInfoId =", id).get();
		return ruleInfo;
	}
	
	public List<RuleInfo> findAll() {
		List<RuleInfo> ruleInfoList = datastore.find(RuleInfo.class).asList();
		return ruleInfoList;
	}
}
