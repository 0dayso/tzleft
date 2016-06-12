package com.travelzen.farerule.jpecker.nest;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.jpecker.JpeckerElite;
import com.travelzen.farerule.mongo.Location;
import com.travelzen.farerule.mongo.morphia.LocationMorphia;
import com.travelzen.farerule.translator.consts.LocationConst;

public class LocationNest {
	
	private static final Logger logger = LoggerFactory.getLogger(JpeckerElite.class);
	
	public static void nest() {
		logger.info("Start nesting locations...");
		LocationMorphia morphia = LocationMorphia.Instance;
		morphia.deleteAll();
		Location location;
		for (Entry<String, String> entry:LocationConst.originMap.entrySet()) {
			location = new Location()
				.setEnLoc(entry.getKey())
				.setCnLoc(entry.getValue());
			morphia.save(location);
		}
		logger.info("Location nesting finish!");
	}

	public static void main(String[] args) {
		nest();
	}
}
