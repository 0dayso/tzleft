package com.travelzen.farerule.mongo.morphia;

import java.util.List;

import com.github.jmkgreen.morphia.Datastore;
import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;
import com.travelzen.farerule.mongo.morphia.util.MongoUtil;

public enum TzRuleMorphia {

	Instance;
	
	private static Datastore datastore;

	static {
    	datastore = MongoUtil.getDatastore();
	}

	public long count() {
		return datastore.createQuery(TzRule.class).countAll();
	}
	
	public void save(TzRule tzRule) {
		TzRuleInfo tzRuleInfo = tzRule.getTzRuleInfo();
		if (findByTzRuleInfo(tzRuleInfo) != null)
			return;
		datastore.save(tzRule);
	}
	
	public void forceSave(TzRule tzRule) {
		TzRuleInfo tzRuleInfo = tzRule.getTzRuleInfo();
		if (findByTzRuleInfo(tzRuleInfo) != null)
			deleteById(tzRule.getTzRuleInfo());
		datastore.save(tzRule);
	}
	
	public void carefulSave(TzRule tzRule) {
		TzRuleInfo tzRuleInfo = tzRule.getTzRuleInfo();
		TzRule tmpTzRule = findByTzRuleInfo(tzRuleInfo);
		if (tmpTzRule != null) {
			if (tmpTzRule.isEdited() == false)
				deleteById(tzRule.getTzRuleInfo());
			else
				return;
		}
		datastore.save(tzRule);
	}

	public void deleteById(TzRuleInfo tzRuleInfo) {
		datastore.delete(datastore.createQuery(TzRule.class).filter("tzRuleInfo =", tzRuleInfo));
	}

	public void deleteAll() {
		datastore.delete(datastore.createQuery(TzRule.class));
	}

	public TzRule findByTzRuleInfo(TzRuleInfo tzRuleInfo) {
		TzRule tzRule = datastore.find(TzRule.class, "tzRuleInfo =", tzRuleInfo).get();
		return tzRule;
	}
	
	public TzRule findByJpeckerId(String id) {
		TzRuleInfo tzRuleInfo = new TzRuleInfo()
			.setRuleSource(RuleSourceEnum.JPECKER)
			.setJpeckerRuleId(id);
		return findByTzRuleInfo(tzRuleInfo);
	}
	
	public TzRule findByPaperfareId(String id) {
		TzRuleInfo tzRuleInfo = new TzRuleInfo()
			.setRuleSource(RuleSourceEnum.PAPERFARE)
			.setJpeckerRuleId(id);
		return findByTzRuleInfo(tzRuleInfo);
	}
	
	public List<TzRule> findAll() {
		List<TzRule> tzRuleList = datastore.find(TzRule.class).asList();
		return tzRuleList;
	}

}
