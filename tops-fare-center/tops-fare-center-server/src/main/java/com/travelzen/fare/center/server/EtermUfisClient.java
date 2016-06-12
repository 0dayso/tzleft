package com.travelzen.fare.center.server;

import java.util.ArrayList;
import java.util.List;

import com.client.ufis.client.APIUfisClient;
import com.common.ufis.util.UfisException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtermUfisClient {
	
    private static Logger logger = LoggerFactory.getLogger(EtermUfisClient.class);
    private APIUfisClient apiUfisClient;

    /**
     * @throws RuntimeException
     */
    public EtermUfisClient() {
        try {
            apiUfisClient = APIUfisClient.getInstance("000000000000000000000001", "SHA255", "API_DOMESTIC", 10);
        } catch (UfisException e) {
            logger.error("Ufis Client初始化异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param officeNo : office号，比如 SHA255
     * @throws RuntimeException
     */
    public EtermUfisClient(String officeId) {
        try {
            apiUfisClient = APIUfisClient.getInstance("000000000000000000000001", officeId, "API_DOMESTIC", 10);
        } catch (UfisException e) {
            logger.error("Ufis Client初始化异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param provider : 供应商ID，比如 0001
     * @param officeNo : office号，比如 SHA255
     * @param role     : 角色名称，比如 API_DEMESTIC
     * @param timeout  : 初始化延长时间，单位：秒，比如 10
     * @throws RuntimeException
     */
    public EtermUfisClient(String provider, String officeNo, String role, int timeout) {
        try {
            apiUfisClient = APIUfisClient.getInstance(provider, officeNo, role, timeout);
        } catch (UfisException e) {
            logger.error("Ufis Client初始化异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 延长超时时间
     * <p>
     * @param time
     */
    public void extendSessionExpire(int time) {
        apiUfisClient.setDelaytime(time);
    }
    
    /**
     * 执行指令
     * <p>
     * @param cmd 指令
     */
    public String executeCmd(String cmd) throws UfisException {
        StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer(cmd));
        logger.info("执行:{},返回的原始文本为:\n{}", cmd, result);
        return result.toString();
    }

    /**
     * 执行AV指令
     * <p>
     * @param avCmd AV指令
     * @param flipOverNum 翻几页
     * @author yiming.yan
     */
    public String executeCmdAv(String avCmd, int flipOverNum) throws UfisException {
        String result = apiUfisClient.sendCodeToServer(avCmd);
        logger.info("执行:{},返回的原始文本为:\n{}", avCmd, result);
        List<String> lines = new ArrayList<String>();
        lines = pickValidAvLines(lines, result.split("[\r\n]"));
        int pageCount = 1;
        while (pageCount++ <= flipOverNum) {
        	lines = pickValidAvLines(lines, executeCmdPn().split("[\r\n]"));
        }
        result = convLinesToStr(lines);
        logger.info("执行:{},flipOverNum:{},返回的原始文本为:\n{}", avCmd, flipOverNum, result);
        return result;
    }

	private static List<String> pickValidAvLines(List<String> lines, String[] rawLines) {
		for (String line:rawLines) {
//			if (!line.trim().startsWith("*"))
				lines.add(line);
		}
		return lines;
	}
	
	private static String convLinesToStr(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for (String line:lines)
			sb.append(line).append("\n");
		return sb.toString();
	}
	
	/**
     * 执行RT指令(包含团队RTN)
     * <p>
     * @param rtCmd RT指令
     */
    public String executeCmdRtAll(String rtCmd) throws UfisException {
        StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer(rtCmd));
        logger.info("执行:{},返回的原始文本为:\n{}", rtCmd, result);
        if (result.toString().trim().startsWith("0.")) {
        	result = new StringBuilder(executeCmdRtn(false));
        }
        while (result.charAt(result.length()-1) == '+') {
            result.append("\n");
            result.append(executeCmdPn());
        }
        logger.info("执行:{},返回的原始文本为:\n{}", rtCmd, result);
        return result.toString();
    }
	
	/**
     * 执行RT指令
     * <p>
     * @param rtCmd RT指令
     * @param flipOver 是否翻页
     */
    public String executeCmdRt(String rtCmd, boolean flipOver) throws UfisException {
        String result = apiUfisClient.sendCodeToServer(rtCmd);
        logger.info("执行:{},返回的原始文本为:\n{}", rtCmd, result);
        while (flipOver && result.endsWith("+")) {
            result += "\n";
            result += executeCmdPn();
        }
        logger.info("执行:{},flipOver:{},返回的原始文本为:\n{}", rtCmd, flipOver, result);
        return result;
    }
    
    /**
     * 执行RTN指令
     * <p>
     * @param flipOver 是否翻页
     */
    public String executeCmdRtn(boolean flipOver) throws UfisException {
        String result = apiUfisClient.sendCodeToServer("RTN");
        logger.info("执行:{},返回的原始文本为:\n{}", "RTN", result);
        while (flipOver && result.endsWith("+")) {
            result += "\n";
            result += executeCmdPn();
        }
        logger.info("执行:{},flipOver:{},返回的原始文本为:\n{}", "RTN", flipOver, result);
        return result;
    }

	/**
     * 执行PN指令
     */
    public String executeCmdPn() throws UfisException {
        StringBuilder result = new StringBuilder(apiUfisClient.sendCodeToServer("PN"));
        logger.info("执行PN,返回的原始文本为:\n{}", result);
        // Ufis的PN结果的首行'-'后缺一个换行符
        if (result.length() >= 80 && result.charAt(79) == '-') {
        	result.insert(80, "\n");
        }
        return result.toString();
    }
    
    /**
     * 执行PB指令
     */
    public String executeCmdPb() throws UfisException {
        String result = apiUfisClient.sendCodeToServer("PB");
        logger.info("执行PB,返回的原始文本为:\n{}", result);
        return result;
    }

    /**
     * 关闭连接
     */
    public void close() {
        apiUfisClient.finish();
    }
}
