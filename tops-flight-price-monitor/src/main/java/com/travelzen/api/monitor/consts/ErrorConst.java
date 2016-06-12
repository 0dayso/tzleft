package com.travelzen.api.monitor.consts;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;

public class ErrorConst {

	public static final HashMap<String, String> errorMap = new HashMap<String, String>(){
		
		private static final long serialVersionUID = 1063297507951055979L;

		{
			//System Error
			put("403", "用户名、密码、IP 错误或无权限");
			put("303", "内部服务错误");
			put("304", "访问速度过快,请稍后再试");
			put("305", "访问次数限制");
			
			//Biz Error
			put("1", "业务异常");
			put("400", "xml内容格式错误");
			put("402", "输入错误");
			
			//AirBook
			put("301", "没有此office权限");
			put("500", "office代码为空");
			put("530", "旅客信息为空");
			put("731", "旅客序号错误");
			put("41040", "指定的航班在指定的日期不执行或指定的舱位已经无法订取");
			
			//AirResRetComplete
			put("700", "office号格式错误");
			put("760", "pnr号格式错误");
			put("9999", "未知错误(IBE异常)");
			put("10060", "pnr不存在");
			put("43001", "提取PNR类型错误");
			put("43010", "无权限提取PNR");
			
			//AirFareFlightShop_I
			put("10733", "调用国际运价错误");
		}
		
	};
	
	public static Pair<Boolean, String> getErrorText(String text) {
		if (text == null)
			return null;
		Matcher matcher = Pattern.compile("\\<Errors\\>(.+?)\\</Errors\\>").matcher(text);
		if (matcher.find())
			return Pair.with(false, matcher.group(1));
		return Pair.with(true, null);
	}
	
	public static String getError(String errorText) {
		if (errorText == null)
			return null;
		// 一些错误类型不属于需监测的错误
		if (errorText.contains("10060"))
			return "NOERROR";
		Pattern pattern1 = Pattern.compile("ShortTextZH=\"(.+?)\"");
		Pattern pattern2 = Pattern.compile("ShortText=\"(.+?)\"");
		Pattern pattern3 = Pattern.compile("(\\d+)");
		Matcher matcher1 = pattern1.matcher(errorText);
		if (matcher1.find()) {
			String error = matcher1.group(1);
			if (error != null)
				return error;
		} else {
			Matcher matcher2 = pattern2.matcher(errorText);
			if (matcher2.find()) {
				String error = matcher2.group(1);
				if (error != null)
					return error;
			} else {
				Matcher matcher3 = pattern3.matcher(errorText);
				if (matcher3.find()) {
					String error = errorMap.get(matcher3.group(1));
					if (error != null)
						return error;
				}
			}
		}
		return errorText;
	}
	
	public static void main(String[] args) {
		String s1 = "<TSK_AirfareFlightShop><TSK_AirfareFlightShopRS>"
				+ "<Errors><Error>-301office permission deniednull</Error></Errors>"
				+ "</TSK_AirfareFlightShopRS></TSK_AirfareFlightShop>";
		String s2 = "<Errors><Error>BUSINESS EXCEPTION SHOP-RQ-015 no access for SHA000-1E, please contact shopping admin @./BusinessLogic/ShopperConfigService/ShopperConfigService.cpp::getShopperConfig</Error></Errors>";
		String s3 = "<TES_AirResRetCompleteRS><Errors><Error Code=\"-10060\" Type=\"PNR_NOT_FOUND\" ShortText=\"pnr does not exist\" ShortTextZH=\"pnr不存在哈\"/></Errors></TES_AirResRetCompleteRS>";
		
		Pair<Boolean, String> p1 = getErrorText(s1);
		System.out.println(p1);
		if (!p1.getValue0())
			System.out.println(getError(p1.getValue1()));
		Pair<Boolean, String> p2 = getErrorText(s2);
		System.out.println(p2);
		if (!p2.getValue0())
			System.out.println(getError(p2.getValue1()));
		Pair<Boolean, String> p3 = getErrorText(s3);
		System.out.println(p3);
		if (!p3.getValue0())
			System.out.println(getError(p3.getValue1()));
	}

}
