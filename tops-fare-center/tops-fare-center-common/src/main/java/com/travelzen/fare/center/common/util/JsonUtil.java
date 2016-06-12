package com.travelzen.fare.center.common.util;

import java.io.IOException;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Json工具类
 * <p>
 * @author yiming.yan
 * @Date Oct 21, 2015
 */
public enum JsonUtil {
	
	;
	
	private static JsonFactory jsonFactory = new JsonFactory();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
     * 将Java对象序列化为json字符串 by Jackson
     * 
     * <p>对于复杂pojo，jackson转化不准确, 请用toJsonByGson
     *
     * @param pojo　需要序列化的对象
     * @param prettyPrint 是否打印优化，即换行
     * @return
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     */
	public static String toJson(Object pojo, boolean prettyPrint) 
			throws IOException, JsonGenerationException, JsonMappingException {
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(sw);
		if (prettyPrint)
			jsonGenerator.useDefaultPrettyPrinter();
		objectMapper.writeValue(jsonGenerator, pojo);
		return sw.toString();
	}
	
	/**
     * 将json字符串反序列化为Java对象 by Jackson
     * 
     * <p>对于复杂pojo，jackson转化不正确, 请用fromJsonByGson
     *
     * @param jsonStr json字符串
     * @param pojoClass　待反序列化的类型
     * @return
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
	public static <T> Object fromJson(String jsonStr, Class<T> pojoClass) 
			throws IOException, JsonParseException, JsonMappingException {
		return objectMapper.readValue(jsonStr, pojoClass);
	}
	
	/**
     * 将json字符串转化为JSONObject对象
     * 
     * @param jsonStr json字符串
     * @return
     * @throws IOException
     */
	public static JSONObject fromJson(String jsonStr) throws JSONException {
		return new JSONObject(jsonStr);
	}
	
	private static Gson gson = new Gson();
	
	/**
     * 将Java对象序列化为json字符串 by gson
     *
     * @param jsonStr json字符串
     * @return
     */
	public static String toJsonByGson(Object pojo){
		return gson.toJson(pojo);
	}
	
	/**
     * 将json字符串反序列化为Java对象 by gson
     *
     * @param jsonStr json字符串
     * @param pojoClass　待反序列化的类型
     * @return
     * @throws JsonSyntaxException
     */
	public static <T> Object fromJsonByGson(String jsonStr, Class<T> pojoClass) throws JsonSyntaxException {
		return gson.fromJson(jsonStr, pojoClass);
	}

}
