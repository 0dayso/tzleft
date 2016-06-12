package com.travelzen.fare.center.mongo.morphia;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.travelzen.fare.center.mongo.util.MorphiaUtil;
import com.travelzen.rosetta.eterm.common.pojo.av.Flight;

/**
 * farecenter数据库
 * <p>
 * Flight表操作类
 * <p>
 * @author yiming.yan
 * @Date Nov 25, 2015
 */
public enum FlightMorphia {
	
	INSTANCE;
	
	private static Datastore datastore;
	
	static {
		datastore = MorphiaUtil.getDatastore();
	}
	
	/**
	 * 全部统计
	 * <p>
	 * @return
	 */
	public long countAll() {
		return datastore.createQuery(Flight.class).countAll();
	}
	
	/**
	 * 统计
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 * @return
	 */
	public long count(String deptDate, String deptAirport, String destAirport) {
		return datastore.createQuery(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport).countAll();
	}
	
	/**
	 * 统计
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 * @param airCompany　航司
	 * @return
	 */
	public long count(String deptDate, String deptAirport, String destAirport, String airCompany) {
		return datastore.createQuery(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport)
				.filter("airCompany", airCompany).countAll();
	}
	
	/**
	 * 插入
	 * <p>
	 * @param flight
	 */
	public void insert(Flight flight) {
		datastore.save(flight);
	}
	
	/**
	 * 插入
	 * <p>
	 * @param flights
	 */
	public void insert(List<Flight> flights) {
		datastore.save(flights);
	}
	
	/**
	 * 清空数据
	 * <p>
	 */
	public void deleteAll() {
		datastore.delete(datastore.createQuery(Flight.class));
	}
	
	/**
	 * 删除
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 */
	public void delete(String deptDate, String deptAirport, String destAirport) {
		datastore.delete(datastore.createQuery(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport));
	}
	
	/**
	 * 删除
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 * @param airCompany　航司
	 */
	public void delete(String deptDate, String deptAirport, String destAirport, String airCompany) {
		datastore.delete(datastore.createQuery(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport)
				.filter("airCompany", airCompany));
	}
	
	/**
	 * 更新
	 * <p>
	 * @param flight
	 */
	public void update(Flight flight) {
		Query<Flight> query = datastore.createQuery(Flight.class)
				.filter("deptDate", flight.getDeptDate())
				.filter("deptAirport", flight.getDeptAirport())
				.filter("destAirport", flight.getDestAirport());
		datastore.updateFirst(query, flight, true);
	}
	
	/**
	 * 查询
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 * @return
	 */
	public List<Flight> find(String deptDate, String deptAirport, String destAirport) {
		return datastore.find(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport).asList();
	}
	
	/**
	 * 查询
	 * <p>
	 * @param deptDate 出发日期
	 * @param deptAirport　出发机场
	 * @param destAirport　到达机场
	 * @param airCompany　航司
	 * @return
	 */
	public List<Flight> find(String deptDate, String deptAirport, String destAirport, String airCompany) {
		return datastore.find(Flight.class)
				.filter("deptDate", deptDate)
				.filter("deptAirport", deptAirport)
				.filter("destAirport", destAirport)
				.filter("airCompany", airCompany).asList();
	}

}
