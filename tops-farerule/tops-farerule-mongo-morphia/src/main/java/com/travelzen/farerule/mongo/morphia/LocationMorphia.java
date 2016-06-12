package com.travelzen.farerule.mongo.morphia;

import java.util.List;

import com.github.jmkgreen.morphia.Datastore;
import com.travelzen.farerule.mongo.Location;
import com.travelzen.farerule.mongo.morphia.util.MongoUtil;

public enum LocationMorphia {

	Instance;
	
	private static Datastore datastore;

	static {
    	datastore = MongoUtil.getDatastore();
	}

	public long count() {
		return datastore.createQuery(Location.class).countAll();
	}
	
	public void save(Location location) {
		String enLoc = location.getEnLoc();
		if (find(enLoc) != null)
			return;
		datastore.save(location);
	}
	
	public void forceSave(Location location) {
		String enLoc = location.getEnLoc();
		if (find(enLoc) != null)
			delete(enLoc);
		datastore.save(location);
	}

	public void delete(String enLoc) {
		datastore.delete(datastore.createQuery(Location.class).filter("enLoc =", enLoc));
	}

	public void deleteAll() {
		datastore.delete(datastore.createQuery(Location.class));
	}

	public Location find(String enLoc) {
		Location location = datastore.find(Location.class, "enLoc =", enLoc).get();
		return location;
	}
	
	public List<Location> findAll() {
		List<Location> locationList = datastore.find(Location.class).asList();
		return locationList;
	}
	
	public List<Location> findNew() {
		List<Location> locationList = datastore.find(Location.class, "cnLoc =", null).asList();
		return locationList;
	}
}
