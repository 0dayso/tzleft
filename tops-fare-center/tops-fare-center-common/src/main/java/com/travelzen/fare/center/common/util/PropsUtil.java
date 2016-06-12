package com.travelzen.fare.center.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 属性文件读取工具类
 * <p>
 * @author yiming.yan
 * @Date Oct 21, 2015
 */
public enum PropsUtil {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
	
	/**
	 * 加载属性文件
	 */
	public static Properties loadProps(String fileName) {
		Properties props = null;
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			if (is == null)
				throw new FileNotFoundException(fileName + " NOT FOUND!");
			props = new Properties();
			props.load(is);
		} catch (IOException e) {
			LOGGER.error("load properties file failure", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error("close input stream failure",e);
				}
			}
		}
		return props;
	}
	
	/**
	 * 获取String型属性（默认值为""）
	 */
	public static String getString(Properties props, String key) {
		return getString(props, key, "");
	}

	/**
	 * 获取String型属性
	 */
	private static String getString(Properties props, String key, String defaultValue) {
		if (props.containsKey(key))
			return props.getProperty(key);
		return defaultValue;
	}
	
	/**
	 * 获取int型属性（默认值为0）
	 */
	public static int getInt(Properties props, String key) {
		return getInt(props, key, 0);
	}

	/**
	 * 获取int型属性
	 */
	private static int getInt(Properties props, String key, int defaultValue) {
		if (props.containsKey(key))
			return Integer.parseInt(props.getProperty(key));
		return defaultValue;
	}
	
	/**
	 * 获取boolean型属性（默认值为false）
	 */
	public static boolean getBoolean(Properties props, String key) {
		return getBoolean(props, key, false);
	}

	/**
	 * 获取boolean型属性
	 */
	private static boolean getBoolean(Properties props, String key, boolean defaultValue) {
		if (props.containsKey(key))
			return Boolean.parseBoolean(props.getProperty(key));
		return defaultValue;
	}

}
