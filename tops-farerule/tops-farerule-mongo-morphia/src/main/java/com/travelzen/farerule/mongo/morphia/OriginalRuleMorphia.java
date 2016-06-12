package com.travelzen.farerule.mongo.morphia;

import java.util.List;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.util.MongoUtil;

public enum OriginalRuleMorphia {

	Instance;
	
	private static Datastore datastore;

	static {
    	datastore = MongoUtil.getDatastore();
	}

	public long count() {
		return datastore.createQuery(OriginalRule.class).countAll();
	}

	public void save(OriginalRule originalRule) {
		String id = originalRule.getId();
		if (findById(id) != null)
			return;
		datastore.save(originalRule);
	}
	
	public void forceSave(OriginalRule originalRule) {
		String id = originalRule.getId();
		if (findById(id) != null)
			deleteById(originalRule.getId());
		datastore.save(originalRule);
	}

	public void deleteById(String id) {
		datastore.delete(datastore.createQuery(OriginalRule.class).filter("id =", id));
	}

	public void deleteAll() {
		datastore.delete(datastore.createQuery(OriginalRule.class));
	}

	public void update(OriginalRule originalRule) {
    	Query<OriginalRule> query = 
    			datastore.createQuery(OriginalRule.class).filter("id =", originalRule.getId());
    	UpdateOperations<OriginalRule> ops = 
    			datastore.createUpdateOperations(OriginalRule.class).set("text", originalRule.getText());
    	datastore.update(query, ops);
	}

	public OriginalRule findById(String id) {
		OriginalRule originalRule = datastore.find(OriginalRule.class, "id =", id).get();
		return originalRule;
	}
	
	public List<OriginalRule> findByAirCompany(String airCompany) {
		List<OriginalRule> originalRuleList = datastore.find(OriginalRule.class, "airCompany =", airCompany).asList();
		return originalRuleList;
	}
	
	public List<OriginalRule> findAll() {
		List<OriginalRule> originalRuleList = datastore.find(OriginalRule.class).asList();
		return originalRuleList;
	}

}
