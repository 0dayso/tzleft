package com.travelzen.farerule.jpecker.client.properties;

import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.framework.config.tops.TopsConfReader;

/**
 * 
 * @author hongqiang.mao
 * 
 * @date 2013-5-8 下午12:50:13
 * 
 * @description
 */
public class PropertiesConfigLoader {

	/**
	 * 配置文件
	 */
	private final static String CONFIG_FILE = "tops-farerule-jpecker-client/jpecker-client.properties";

	/**
	 * thrift服务ip
	 */
	private final static String THRIFT_SERVER_IP = "farerule.jpecker.server.ip";
	/**
	 * thrift服务端口号
	 */
	private final static String THRIFT_SERVER_PORT = "farerule.jpecker.server.port";

	/**
	 * thrift服务名称
	 */
	private final static String THRIFT_SERVER_NAME = "zookeeper.name";

	/**
	 * 前缀
	 */
	private final static String THRIFT_PREFIX = "zookeeper.prefix";
	

	private static PropertiesConfigLoader serviceConfig = new PropertiesConfigLoader();


	/**
	 * single object model
	 * 
	 * @return
	 */
	public static PropertiesConfigLoader getInstance() {
		return serviceConfig;
	}

	/**
	 * thrift服务ip
	 * 
	 * @return
	 */
	public String getThriftServerIP() {
		return TopsConfReader.getConfContent(CONFIG_FILE, THRIFT_SERVER_IP, ConfScope.R);
	}

	/**
	 * thrift服务端口号
	 * 
	 * @return
	 */
	public String getThriftServerPort() {
		return TopsConfReader.getConfContent(CONFIG_FILE, THRIFT_SERVER_PORT, ConfScope.R);
	}

	/**
	 * thrift服务名称
	 * 
	 * @return
	 */
	public String getThriftServerName() {
		return TopsConfReader.getConfContent(CONFIG_FILE, THRIFT_SERVER_NAME, ConfScope.R);
	}
	
	
	/**
	 * thrift前缀
	 * @return
	 */
	public String getThriftPrefix() {
		return TopsConfReader.getConfContent(CONFIG_FILE, THRIFT_PREFIX, ConfScope.R);
	}
}