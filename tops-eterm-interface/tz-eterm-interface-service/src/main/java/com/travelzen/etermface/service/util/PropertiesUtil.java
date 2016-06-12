package com.travelzen.etermface.service.util;

import com.travelzen.framework.core.util.StringUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PropertiesUtil {
	private static Logger logger = LoggerFactory
			.getLogger(PropertiesUtil.class);
	private static final String[] files = { "retCode", "auth" };
	private static Properties properties = new Properties();
	private static long lastLoadTime = new Date().getTime();
	private static long refreshInterval = 1000 * 60;
	static {
		load();
	}

	/**
	 * 
	 * description: 从classpath上加载指定的后缀为properties配置文件，若某个配置文件不存在，直接跳过加载下一个配置文件
	 */
	@SuppressWarnings("deprecation")
	private synchronized static void load() {
		properties.clear();
		InputStream in = null;
		for (String file : files) {
			URL url = PropertiesUtil.class.getClassLoader().getResource(
					file + ".properties");
			try {
				String path = URLDecoder.decode(url.getPath());
				int index = path.indexOf("!/");
				if (index != -1) {
					path = path.substring(0, index);
					JarFile earFile = new JarFile(path.substring(path
							.indexOf("/")));
					JarEntry jarEntry = earFile.getJarEntry(file
							+ ".properties");
					in = earFile.getInputStream(jarEntry);
				} else
					in = new FileInputStream(path);
				properties.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("加载配置文件" + file + "错误", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
			}
		}
	}

	/*
	 * 从消息定义文件中取出code所对应的消息
	 */
	@SuppressWarnings("deprecation")
	public synchronized static String getValue(String key) {
		refresh();
		return StringUtil.filterNull(properties.getProperty(key)).trim();
	}

	/**
	 * 
	 * description: 从消息定义文件中取出code所对应的消息, args为占位符的实际值
	 * 
	 * @param args
	 * @return 消息
	 */
	@SuppressWarnings("deprecation")
	public synchronized static String getValue(String key, Object[] args) {
		refresh();
		return StringUtil.filterNull(MessageFormat.format(getValue(key), args))
				.trim();
	}

	/**
	 * 
	 * description:每隔一段时间重新加载配置文件
	 */
	private synchronized static void refresh() {
		long now = new Date().getTime();
		if ((now - lastLoadTime) > refreshInterval) {
			load();
			lastLoadTime = now;
		}
	}

	public static String getPropertyFromResource(String resourcePath, String key) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();

		Resource rc = ctx.getResource(resourcePath);

		InputStream input = null;
		try {
			input = rc.getInputStream();
		} catch (IOException e1) {
			logger.error(e1.getLocalizedMessage());
			e1.printStackTrace();
			return null;
		}
		if (input != null)
			try {
				Properties properties = new Properties();
				properties.load(input);
				return properties.getProperty(key).trim();
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		return null;
	}

	public static String getProperty(String filePath, String key) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();

		Resource rc = ctx.getResource("/" + filePath);

		InputStream input = null;
		try {
			input = rc.getInputStream();
		} catch (IOException e1) {
			logger.error(e1.getLocalizedMessage());
			e1.printStackTrace();
			return null;
		}
		// Object.class.getResourceAsStream("/" + filePath);
		if (input != null)
			try {
				Properties properties = new Properties();
				properties.load(input);
				return properties.getProperty(key).trim();
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, String> mapProperties(String filePath) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();

		Resource rc = ctx.getResource("/" + filePath);

		InputStream input = null;
		try {
			input = rc.getInputStream();
		} catch (IOException e1) {
			logger.error(e1.getLocalizedMessage());
			e1.printStackTrace();
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		if (input != null)
			try {
				Properties properties = new Properties();
				properties.load(input);
				Enumeration enumeration = properties.propertyNames();
				while (enumeration.hasMoreElements()) {
					String key = (String) enumeration.nextElement();
					map.put(key, properties.getProperty(key));
				}
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		return map;
	}

	public static XMLConfiguration getXMLConfiguration(String irsessionConfPath)
			throws IOException, ConfigurationException {

		ClassPathResource resource = new ClassPathResource(irsessionConfPath);

		XMLConfiguration config = new XMLConfiguration();
		config.setFileName(resource.getFile().getAbsolutePath());
		config.setDelimiterParsingDisabled(true);
		config.setExpressionEngine(new XPathExpressionEngine());
		
		config.load();
		return config;
	}
}
