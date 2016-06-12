package com.travelzen.farerule.jpecker.client.properties;


/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-4 下午10:08:53
 * 
 * @description
 */
public class ConstProperties {
	/**
	 * thrift服务ip
	 */
	public static final String THRIFT_SERVER_IP = PropertiesConfigLoader.getInstance().getThriftServerIP();
	/**
	 * thrift服务端口号
	 */
	public static final String THRIFT_SERVER_PORT = PropertiesConfigLoader.getInstance().getThriftServerPort();

	/**
	 * thrift服务端名称
	 */
	public static final String THRIFT_SERVER_NAME = PropertiesConfigLoader.getInstance().getThriftServerName();

	/**
	 * Pnr prefix
	 */
	public static final String THRIFT_PREFIX = PropertiesConfigLoader.getInstance().getThriftPrefix();
}
