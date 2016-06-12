package com.travelzen.etermface.service;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.client.ufis.client.APIUfisClient;
import com.common.ufis.util.UfisException;
import com.travelzen.framework.core.exception.BizException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 杨果 15/7/20 10:33
 * <p>
 * yiming.yan ~2016/04
 */
public class EtermUfisClient implements Closeable {
	
	private static Logger logger = LoggerFactory.getLogger(EtermUfisClient.class);
	private APIUfisClient apiUfisClient;

	public EtermUfisClient() throws UfisException {
		apiUfisClient = new APIUfisClient("000000000000000000000001", "SYS_SHA255", "API_DOMESTIC", 30);
	}

	public EtermUfisClient(String officeId) throws UfisException {
		apiUfisClient = new APIUfisClient("000000000000000000000001", "SYS_" + officeId, "API_DOMESTIC", 30);
	}
	
	public EtermUfisClient(int timeout) throws UfisException {
		apiUfisClient = new APIUfisClient("000000000000000000000001", "SYS_SHA255", "API_DOMESTIC", timeout);
	}
	
	public EtermUfisClient(String officeId, int timeout) throws UfisException {
		apiUfisClient = new APIUfisClient("000000000000000000000001", "SYS_" + officeId, "API_DOMESTIC", timeout);
	}
	
	public EtermUfisClient(String officeId, String role) throws UfisException {
		apiUfisClient = new APIUfisClient("000000000000000000000001", "SYS_" + officeId, role, 30);
	}

	/**
	 * @param provider : 供应商ID，比如 0001
	 * @param officeNo : office号，比如 SHA255
	 * @param role	 : 角色名称，比如 API_DOMESTIC
	 * @param timeout  : 初始化延长时间，单位：秒，比如 10
	 */
	public EtermUfisClient(String provider, String officeNo, String role, int timeout) throws UfisException {
		apiUfisClient = new APIUfisClient(provider, "SYS_" + officeNo, role, timeout);
	}

	/**
	 * 延长超时时间
	 *
	 * @param time
	 */
	public void extendSessionExpire(int time) {
		apiUfisClient.setDelaytime(time);
	}

	/**
	 * 执行指令
	 * <p>
	 * @param cmd	  命令
	 * @param flipOver 是否翻页
	 */
	public String execCmd(String cmd, boolean flipOver) throws UfisException {
		StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer(cmd));
		logger.info("执行:{},返回的原始文本为:\n{}", cmd, result);
		// 偶有指令结果的首行'-'后缺一个换行符
		if (result.length() >= 80 && result.charAt(79) == '-') {
			if (flipOver) {
				result = new StringBuilder(execPb());
			} else {
				result.replace(79, 80, "\r");
			}
		}
		if (flipOver) {
			while (result.charAt(result.length()-1) == '+') {
				result.deleteCharAt(result.length()-1);
				result.append("\r");
				result.append(execPn());
			}
		}
		logger.info("执行:{},flipOver:{},返回的完整文本为:\n{}", cmd, flipOver, result);
		return result.toString();
	}
	
	/**
	 * 执行指令
	 * <p>
	 * @param cmd 指令
	 */
	public String execCmd(String cmd) throws UfisException {
		StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer(cmd));
		logger.info("执行:{},返回的原始文本为:\n{}", cmd, result);
		// 偶有指令结果的首行'-'后缺一个换行符
		if (result.length() >= 80 && result.charAt(79) == '-') {
			result.replace(79, 80, "\r");
		}
		return result.toString();
	}
	
	/**
	 * 执行AV指令
	 * <p>
	 * @param avCmd AV指令
	 * @param flipOverNum 翻几页
	 * @author yiming.yan
	 */
	public String execAv(String avCmd, int flipOverNum) throws UfisException {
		String result = apiUfisClient.sendCodeToServer(avCmd);
		logger.info("执行:{},返回的原始文本为:\n{}", avCmd, result);
		List<String> lines = new ArrayList<String>();
		lines = pickValidAvLines(lines, result.split("[\r\n]"));
		int pageCount = 1;
		while (pageCount++ <= flipOverNum) {
			lines = pickValidAvLines(lines, execPn().split("[\r\n]"));
		}
		result = convLinesToStr(lines);
		logger.info("执行:{},flipOverNum:{},返回的完整文本为:\n{}", avCmd, flipOverNum, result);
		return result;
	}

	private static List<String> pickValidAvLines(List<String> lines, String[] rawLines) {
		for (String line:rawLines) {
			lines.add(line);
		}
		return lines;
	}
	
	private static String convLinesToStr(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for (String line:lines)
			sb.append(line).append("\r");
		return sb.toString();
	}
	
	/**
	 * 执行RT指令(包含团队RTN)
	 *
	 * @param rtCmd RT指令
	 */
	private static final Pattern GROUP_PATTERN = Pattern.compile("(?:^|\n|\r) *0\\.\\d+.+ NM\\d+");
	public String execRtAll(String rtCmd) throws UfisException {
		StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer(rtCmd));
		logger.info("执行:{},返回的原始文本为:\n{}", rtCmd, result);
		Matcher group_matcher = GROUP_PATTERN.matcher(result.toString());
		if (group_matcher.find()) {
			result = new StringBuilder(execRtn(false));
		}
		while (result.charAt(result.length()-1) == '+') {
			result = result.replace(result.length()-1, result.length(), "\r");
			result.append(execPn());
		}
		logger.info("执行:{},返回的完整文本为:\n{}", rtCmd, result);
		return result.toString();
	}
	
	/**
	 * 执行RT指令
	 *
	 * @param pnrNo pnr号
	 * @param flipOver 是否翻页
	 */
	public String execRt(String pnrNo, boolean flipOver) throws UfisException {
		String result = apiUfisClient.sendCodeToServer("RT " + pnrNo);
		logger.info("执行:{},返回的原始文本为:\n{}", "RT " + pnrNo, result);
		while (flipOver && result.endsWith("+")) {
			result = result.substring(0, result.length()-1);
			result += "\r";
			result += execPn();
		}
		logger.info("执行:{},flipOver:{},返回的完整文本为:\n{}", "RT " + pnrNo, flipOver, result);
		return result;
	}
	
	/**
	 * 执行RTN指令
	 *
	 * @param flipOver 是否翻页
	 */
	public String execRtn(boolean flipOver) throws UfisException {
		String result = apiUfisClient.sendCodeToServer("RTN");
		logger.info("执行:{},返回的原始文本为:\n{}", "RTN", result);
		while (flipOver && result.endsWith("+")) {
			result = result.substring(0, result.length()-1);
			result += "\r";
			result += execPn();
		}
		logger.info("执行:{},flipOver:{},返回的完整文本为:\n{}", "RTN", flipOver, result);
		return result;
	}

	/**
	 * 执行PN指令
	 */
	public String execPn() throws UfisException {
		StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer("PN"));
		logger.info("执行PN,返回的原始文本为:\n{}", result);
		// Ufis的PN结果的首行'-'后缺一个换行符
		if (result.length() >= 80 && result.charAt(79) == '-') {
			result = result.replace(79, 80, "\r");
		}
		return result.toString();
	}
	
	/**
	 * 执行PB指令
	 */
	public String execPb() throws UfisException {
		String result = apiUfisClient.sendCodeToServer("PB");
		logger.info("执行PB,返回的原始文本为:\n{}", result);
		return result;
	}
	
	/**
	 * 先执行RT，再执行XEPNR指令
	 *
	 * @param office office号
	 * @param ownOffice 是否本office创建的pnr
	 */
	public String cancelPnr(String office, boolean ownOffice) throws UfisException {
		String cancelCmd = "XEPNR\\";
		if (!ownOffice && null != office)
			cancelCmd += office;
		String result = apiUfisClient.sendCodeToServer(cancelCmd);
		logger.info("执行:{},返回的原始文本为:\n{}", cancelCmd, result);
		return result;
	}
	
	public String cancelPnr() throws UfisException {
		String result = apiUfisClient.sendCodeToServer("XEPNR\\");
		logger.info("执行:{},返回的原始文本为:\n{}", "XEPNR\\", result);
		return result;
	}

	/**
	 * 执行QTE指令
	 *
	 * @param carrier
	 * @param type
	 */
	public String execQte(List<String> carrier, String type) throws UfisException {
		String qte = "";

		int carrierIdx = 0;
		String qtesult = null;

		NXT_CR:
		for (int carrierLoop = 0; carrierLoop < carrier.size(); carrierLoop++) {
			SESSION_LOCK:
			for (int sessionLockCnt = 0; sessionLockCnt < 3; sessionLockCnt++) {
				if (StringUtils.isEmpty(qte)) {
					//1st try
					qtesult = internalQte(carrier.get(carrierIdx), type);
					if (null != qtesult) {
						qte = qtesult;
					} else {
						continue NXT_CR;
					}
				} else if (StringUtils.contains(qte, "SESSION CURRENTLY LOCKED")) {
					qtesult = internalQte(carrier.get(carrierIdx), "");
					if (null != qtesult) {
						qte = qtesult;
					} else {
						continue NXT_CR;
					}
					continue SESSION_LOCK;
				} else if (StringUtils.contains(qte, "NO INTERLINE AGREEMENT FOR") 
						|| StringUtils.contains(qte, "UNABLE TO PROCESS - MANUAL PRICING REQUIRED")) {
					carrierIdx++;
					if (carrierIdx >= carrier.size()) {
						break NXT_CR;
					}
					qtesult = internalQte(carrier.get(carrierIdx), "");
					if (null != qtesult) {
						qte = qtesult;
					} else {
						continue NXT_CR;
					}
				} else {
					break NXT_CR;
				}
			}
		}

		return qte;
	}
	
	private String internalQte(String carrier, String type) throws UfisException {
		try {
			return execCmd("QTE:" + type + "/" + carrier, true);
		} catch (BizException be) {
			logger.error("internalQte err:{}", be);
			return null;
		}
	}
	
	/**
	 * 封口
	 */
	public String heal() throws UfisException {
		return apiUfisClient.sendCodeToServer("@I");
	}
	
	/**
	 * 还原
	 */
	public void resume() throws UfisException {
		String retText = null;
		for (int index = 0; index < 3; index++) {
			retText = apiUfisClient.sendCodeToServer("IG");
            if (null != retText && "NO PNR".equalsIgnoreCase(retText.trim())) {
                break;
            }
        }
	}

	/**
	 * 关闭链接
	 */
	public void close() {
		apiUfisClient.finish();
	}
}
