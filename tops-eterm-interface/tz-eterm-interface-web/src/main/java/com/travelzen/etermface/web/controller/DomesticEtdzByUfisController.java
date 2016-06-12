package com.travelzen.etermface.web.controller;

import com.common.ufis.util.UfisException;
import com.google.common.collect.Lists;
import com.travelzen.etermface.common.pojo.EtdzResponse;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.util.EtermErrorCode;
import com.travelzen.framework.core.json.JsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/1/19
 * Time:下午1:11
 * <p/>
 * Description:
 * <p/>
 * 国内自动出票
 * <p/>
 * 自动出票要非常注意，对应行所在的页码，否者会出票失败。
 * 状态机如果出现环状结构，请注意在中间加一个计数统计功能，达到阀值就跳出，否者容易出现死循环。
 */
@Controller
public class DomesticEtdzByUfisController {
    private static Logger logger = LoggerFactory.getLogger(DomesticEtdzByUfisController.class);


    /**
     * @param request
     * @param pnr      需要出票的PNR
     * @param officeId 出票的offcie号
     * @param price    出票的价格
     * @param printId  打印架终端号
     * @return
     */
    @RequestMapping(value = "/etdz", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String execute(HttpServletRequest request,
                          @RequestParam String pnr,
                          @RequestParam String officeId,
                          @RequestParam String priceBase,
                          @RequestParam String price,
                          @RequestParam String printId) {
        logger.info("/etdz接口请求参数:pnr={},officeId={},priceBase={},price={},printId={}", pnr, officeId, priceBase, price, printId);
        EtdzResponse etdzResponse = null;
        if (pnr == null || officeId == null || price == null || printId == null) {
        	etdzResponse = new EtdzResponse(false, "参数不合法,请检查请求参数");
        } else {
            Info info = new Info();
            info.setPnr(pnr);
            info.setOfficeId(officeId);
            info.setPriceBase(priceBase);
            info.setPrice(price);
            info.setPrintId(printId);
            info.setMaxTimes(2);
            info.setOfficeId(officeId);
            etdzResponse = States.execute(info);
        }
        String etdzResponseJsonStr = null;
		try {
			etdzResponseJsonStr = JsonUtil.toJson(etdzResponse, false);
		} catch (IOException e) {
			logger.error("etdzResponse to Json error:{}", e.getStackTrace().toString());
		}
        return etdzResponseJsonStr;
    }


    //航班信息行所对应的正则表达式
    private static Pattern flightPattern = Pattern.compile("(\\d).+HK\\d{1}\\s+\\d{4}\\s+\\d{4}.+");
    private static Pattern rrSuccessPattern = Pattern.compile("(\\d).+RR\\d{1}\\s+\\d{4}\\s+\\d{4}.+");
    //出票时限对应的正则表达式
    private static Pattern tlPattern = Pattern.compile("(\\d+).TL/.+");

    public enum States implements State {
        Begin {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Begin);
                try {
                    EtermUfisClient client = new EtermUfisClient(context.getInfo().getOfficeId());
                    context.setClient(client);
                    context.setNextState(Rt);
                    logger.info("成功:状态机状态->Begin,作用->EtermWebClient创建链接,PNR={}", context.getInfo().getPnr());
                } catch (Exception e) {
                    context.setNextState(Counter);
                    logger.info("失败:状态机状态->Begin,PNR={},执行异常:{}", context.getInfo().getPnr(), e);
                    context.setErrorMsg("失败:处于起始状态,PNR=" + context.getInfo().getPnr() + ",执行异常:" + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Error {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Error);
                context.setNextState(null);
                context.getClient().close();
                logger.warn("成功:状态机状态->Error,作用->EtermWebClient关闭链接,PNR={}", context.getInfo().getPnr());
                logger.info("-----------------------");
                return false;
            }
        },
        Success {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Success);
                context.setNextState(null);
                try {
                    context.getClient().close();
                } catch (Exception e) {
                    logger.warn("自动出票成功，但是关闭session出现异常，不影响自动出票", e);
                }
                logger.info("成功:状态机状态->Success,作用->EtermWebClient关闭链接,PNR={}", context.getInfo().getPnr());
                logger.info("-----------------------");
                return false;
            }
        },
        Rt {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Rt);
                try {
                	String rtText = context.getClient().execRt(context.getInfo().getPnr(), true);
                    if (null != rtText) {
                        String returnValue = rtText.replaceAll("\r", "\n");
                        logger.info("状态机状态->Rt,执行结果:\n{}", returnValue);

                        // 判断是否已出票
                        if (returnValue.contains("ELECTRONIC TICKET PNR")) {
                        	logger.info("PNR已出票");
                            context.setErrorMsg("执行Rt,PNR已出票,PNR=" + context.getInfo().getPnr());
                            context.setNextState(Error);
                            logger.info("-----------------------");
                            return true;
                    	}
                        
                        // 在做价格前，判断PNR中是否已有价格，若已有价格则直接出票
                        if (returnValue.contains(".FC/A") || returnValue.contains(".FN/A") || returnValue.contains(".FP/")) {
                    		logger.info("PNR已有价格, 直接出票");
                            context.setNextState(Etdz);
                            logger.info("成功:状态机状态->RT,作用->获取PNR文本,PNR={}", context.getInfo().getPnr());
                            logger.info("-----------------------");
                            return true;
                    	}

                        String[] lines = returnValue.split("\n");
                        for (String line : lines) {
                            String tmp = line.trim();
                            Matcher flightMatcher = flightPattern.matcher(tmp);
                            Matcher tlMatcher = tlPattern.matcher(tmp);
                            if (flightMatcher.find()) {
                                context.getFlightNoS().add(Integer.parseInt(flightMatcher.group(1)));
                            }
                            if (tlMatcher.find()) {
                                context.setTlNo(Integer.parseInt(tlMatcher.group(1)));
                            }
                        }


                        if (context.getFlightNoS().size() == 0) {
                            logger.warn("失败:状态机状态->Rt,作用->获取航班的行号,PNR={}", context.getInfo().getPnr());
                            context.setErrorMsg("执行Rt,获取航班的行号出错,PNR=" + context.getInfo().getPnr());
                            context.setNextState(Error);
                        } else if (context.getTlNo() <= 0) {
                            logger.warn("失败:状态机状态->Rt,作用->获取出票时限的行号,PNR={}", context.getInfo().getPnr());
                            context.setErrorMsg("执行Rt,获取出票时限的行号出错,PNR=" + context.getInfo().getPnr());
                            context.setNextState(Error);
                        } else {
                            logger.info("成功:状态机状态->Rt,作用->获取自动出票需要用到的行号,PNR={}", context.getInfo().getPnr());
                            context.setNextState(Rr);
                        }
                    } else {
                    	logger.warn("RT请求失败");
                    	context.setErrorMsg("RT请求失败");
                        context.setNextState(Counter);
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Pf {
            //特别注意，rr执行前一定要留在第一页，否者会出现ELE NBR
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Pf);
                try {
                    String retText = context.getClient().execCmd("PF");
                    if (null != retText) {
                        String returnValue = retText.replaceAll("\r", "\n");
                        logger.info("状态机状态->Pf,执行结果:\n{}", returnValue);

                        context.setNextState(Rr);
                    } else {
                    	logger.warn("PF请求失败");
                    	context.setErrorMsg("PF请求失败");
                        context.setNextState(Counter);
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Rr {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Rr);
                try {
                    for (Integer no : context.flightNoS) {
                    	String retText = context.getClient().execCmd(no + "RR");
                        if (null != retText) {
                            String returnValue = retText.replaceAll("\r", "\n");
                            logger.info("状态机状态->Rr,执行结果:\n{}", returnValue);

                            //判断指令是否执行成功
                            boolean isRrSuccess = false;
                            String[] lines = returnValue.split("\n");
                            for (String line : lines) {
                                Matcher matcher = rrSuccessPattern.matcher(line);
                                if (matcher.find()) {
                                    isRrSuccess = true;
                                    break;
                                }
                            }
                            if (isRrSuccess) {
                                logger.info("成功:状态机状态->Rr,作用->选择出票航班,PNR={}", context.getInfo().getPnr());
                                context.setNextState(Xe);
                            } else {
                                if (returnValue.contains(EtermErrorCode.ELE_NBR.getCode())) {
                                    context.setCurrentState(Pf);
                                }
                                logger.warn("失败:状态机状态->Rr,作用->选择出票航班,PNR={}", context.getInfo().getPnr());
                                context.setErrorMsg("执行Rr,选择出票航班出错,PNR=" + context.getInfo().getPnr());
                                context.setNextState(Counter);
                            }
                        } else {
                        	logger.warn("RR请求失败");
                        	context.setErrorMsg("RR请求失败");
                            context.setNextState(Counter);
                        }
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Xe {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Xe);
                try {
//                	String retText = context.getClient().execCmd("xe" + context.getTlNo());
//                    if (null != retText) {
//                        String returnValue = retText.replaceAll("\r", "\n");
//                        logger.info("状态机状态->Xe,执行结果:\n{}", returnValue);
//
//                        boolean isXeSuccess = true;
//                        String[] lines = returnValue.split("\n");
//                        for (String line : lines) {
//                            Matcher matcher = tlPattern.matcher(line.trim());
//                            if (matcher.find()) {
//                                isXeSuccess = false;
//                                break;
//                            }
//                        }
//
//                        if (isXeSuccess) {
//                            logger.warn("成功:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
//                            context.setNextState(Pat);
//                        } else {
//                        	logger.warn("失败:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
//                        	context.setErrorMsg("执行Xe,删除出票时效出错,PNR=" + context.getInfo().getPnr());
//                            context.setNextState(Counter);
//                        }
//                    } else {
//                        logger.warn("XE请求失败");
//                        context.setErrorMsg("XE请求失败");
//                        context.setNextState(Counter);
//                    }
                    
                    // TL项不在当前页的情况下XE会报ELE　NBR，因此RT后每PN一次XE一次，直到删除TL项
                    boolean isXeSuccess = false;
                    String rtText = context.getClient().execCmd("rt").replaceAll("\r", "\n");
                    rtText = context.getClient().execCmd("pb").replaceAll("\r", "\n");
                    logger.info("状态机状态->Xe,执行rt 结果:\n{}", rtText);
                    String xeText = context.getClient().execCmd("xe " + context.getTlNo()).replaceAll("\r", "\n");
                    logger.info("状态机状态->Xe,执行xe 结果:\n{}", xeText);
                    if (!xeText.contains(EtermErrorCode.ELE_NBR.getCode())) {
                    	boolean hasTl = false;
                    	String[] lines = xeText.split("\n");
                    	for (String line : lines) {
                            Matcher matcher = tlPattern.matcher(line.trim());
                            if (matcher.find()) {
                            	hasTl = true;
                                break;
                            }
                        }
                    	if (!hasTl)
                    		isXeSuccess = true;
                    }
                    while (!isXeSuccess && rtText.charAt(rtText.length()-1) == '+') {
                    	rtText = context.getClient().execCmd("pn").replaceAll("\r", "\n");
                        logger.info("状态机状态->Xe,执行pn 结果:\n{}", rtText);
                        xeText = context.getClient().execCmd("xe " + context.getTlNo()).replaceAll("\r", "\n");
                        logger.info("状态机状态->Xe,执行xe 结果:\n{}", xeText);
                        if (!xeText.contains(EtermErrorCode.ELE_NBR.getCode())) {
                        	boolean hasTl = false;
                        	String[] lines = xeText.split("\n");
                        	for (String line : lines) {
                                Matcher matcher = tlPattern.matcher(line.trim());
                                if (matcher.find()) {
                                	hasTl = true;
                                    break;
                                }
                            }
                        	if (!hasTl)
                        		isXeSuccess = true;
                        }
					}

                    if (isXeSuccess) {
                        logger.warn("成功:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
                        context.setNextState(Pat);
                    } else {
                        logger.warn("失败:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
                        context.setErrorMsg("执行Xe,删除出票时效出错,PNR=" + context.getInfo().getPnr());
                        context.setNextState(Counter);
                    }
                    
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Pat {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Pat);
                //需要将+号转义
                if (context.getInfo().getPriceBase().contains("+")) {
                    context.getInfo().setPriceBase(context.getInfo().getPriceBase().replaceAll("\\+", "\\\\+"));
                }
                //需要配置的运价基础和运价的正则表达式
                Pattern patPattern = Pattern.compile("(\\d{2}).+" + context.getInfo().getPriceBase() + ".+FARE:CNY" + context.getInfo().getPrice() + ".+");
                //Qunar没有传运价基础，根据订单中的票面价和pat：a的结果做比对，选择票面一致的
                Pattern patPattern_qunar = Pattern.compile("(\\d{2}).+FARE:CNY" + context.getInfo().getPrice());
                try {
                    //首先pat报价，然后进行运价基础和运价匹配
                	String retText = context.getClient().execCmd("PAT:A", true);
                    //判断运价基础和运价是否在pat报价中
                    if (null != retText) {
                        String returnValue = retText.replaceAll("\r", "\n");
                        logger.info("状态机状态->Pat,执行结果:{}", returnValue);

                        boolean isPatSuccess = false;
                        String[] lines = returnValue.split("\n");
                        
                        for (int i = 0; i < lines.length; i++) {
                            String line = lines[i].trim();
                            Matcher matcher = patPattern.matcher(line);
                            if (matcher.find()) {
                                isPatSuccess = true;
                                //运价基础和运价都匹配上的行号
                                context.setSfcNo(matcher.group(1));
                                break;
                            }
                        }
                        if (!isPatSuccess) {
                        	for (int i = 0; i < lines.length; i++) {
                                String line = lines[i].trim();
                                Matcher matcher = patPattern_qunar.matcher(line);
                                if (matcher.find()) {
                                    isPatSuccess = true;
                                    //运价匹配上的行号
                                    context.setSfcNo(matcher.group(1));
                                    break;
                                }
                            }
                        }

                        if (isPatSuccess) {
                            context.setNextState(Sfc);
                            logger.info("成功:状态机状态->Pat,作用->获取报价,PNR={}", context.getInfo().getPnr());
                        } else {
                        	logger.info("失败:状态机状态->Pat,作用->获取报价,PNR={}", context.getInfo().getPnr());
                        	context.setErrorMsg("执行Pat,获取报价出错,PNR=" + context.getInfo().getPnr());
                            context.setNextState(Counter);
                        }
                    } else {
                        logger.warn("PAT请求失败");
                        context.setErrorMsg("PAT请求失败");
                        context.setNextState(Counter);
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Sfc {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Sfc);
                try {
                    //选择运价
                	String retText = context.getClient().execCmd("SFC:" + context.getSfcNo(), true);
                    //sfc跟黑屏不一样，黑屏中会返回PNR中的内容，实际该指令返回为空，特别注意
                    if (null != retText) {
                        String returnValue = retText.replaceAll("\r", "\n");
                        logger.info("状态机状态->Sfc,执行结果:\n{}", returnValue);

                        context.setNextState(Etdz);
                        logger.info("成功:状态机状态->Sfc,作用->选择运价,PNR={}", context.getInfo().getPnr());
                    } else {
                        logger.warn("SFC请求失败");
                        context.setErrorMsg("SFC请求失败");
                        context.setNextState(Counter);
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Etdz {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Etdz);
                try {
                	String retText = context.getClient().execCmd("ETDZ " + context.getInfo().getPrintId());
                    if (null != retText) {
                        String returnValue = retText.replaceAll("\r", "\n");
                        logger.info("状态机状态->Etdz,执行结果:\n{}", returnValue);

                        boolean isSuccess = false;
                        if (retText.contains("CNY") && retText.contains(context.getInfo().getPnr())) {
                            String[] lines = returnValue.split("\n");
                            if (lines.length == 2) {
                                isSuccess = true;
                            }
                        } else if (retText.contains("ELECTRONIC TICKET ISSUED")) {
                            isSuccess = true;
                        } else if (retText.contains("NO PNR")) {
                            isSuccess = true;
                        }

                        if (isSuccess) {
                            context.setNextState(Success);
                            logger.info("成功:状态机状态->Etdz,作用->打印机票,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                        } else {
                            if (returnValue.contains(EtermErrorCode.STOCK.getCode()) || returnValue.contains(EtermErrorCode.OUT_OF_STOCK.getCode())) {
                                logger.warn("失败:打票机没票了,请TN重新上新票号");
                                context.setErrorMsg("失败:打票机没票了,请TN重新上新票号");
                                context.setNextState(Error);
                            } else {
                            	context.setErrorMsg("Etdz错误：" + returnValue);
                                context.setNextState(Counter);
                            }
                            logger.info("失败:状态机状态->Etdz,作用->打印机票,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                        }
                    } else {
                        logger.warn("Etdz请求失败");
                        context.setErrorMsg("Etdz请求失败");
                        context.setNextState(Counter);
                    }
                } catch (UfisException e) {
                    context.setNextState(Error);
                    logger.warn("UfisException,{}", e);
                    context.setErrorMsg("UfisException," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Counter {
            public boolean process(Context context) {
                logger.info("************************");
                context.getClient().extendSessionExpire(2000);
                //判断上一次出错的状态是否跟这次出错的状态一致，如果不一致将其设置为本次，并将计数改为0
                if (!context.getCurrentState().equals(context.getErrorState())) {
                    context.setErrorState(context.getCurrentState());
                    context.setTimes(0);
                }

                if (context.getTimes() >= context.getInfo().getMaxTimes()) {
                    logger.warn("失败:状态机状态->Counter,作用->对执行状态进行计数,达到最大的{}次重试", context.getInfo().getMaxTimes());
                    context.setErrorState(null);
                    context.setTimes(0);
                    context.setCurrentState(Counter);
                    context.setNextState(Error);
                } else {
                    int times = context.getTimes() + 1;
                    logger.warn("成功:状态机状态->Counter,作用->对执行状态进行计数,第{}次重试.", times);
                    context.setNextState(context.getCurrentState());
                    context.setCurrentState(Counter);
                    context.setTimes(times);
                }
                logger.info("-----------------------");
                return true;
            }
        };


        public static EtdzResponse execute(Info info) {
            Context context = new Context();
            context.setInfo(info);
            context.setNextState(Begin);
            EtdzResponse etdzResponse = null;
            while (context.getNextState().process(context)) ;
            if (context.getCurrentState().equals(Success)) {
            	etdzResponse = new EtdzResponse(true, null);
            } else {
            	etdzResponse = new EtdzResponse(false, context.getErrorMsg());
            }
            return etdzResponse;
        }
    }

    /**
     * 状态机接口
     */
    public interface State {
        boolean process(Context context);
    }

    /**
     * 状态机上下文
     */
    public static class Context {
        /**
         * 传入状态机的参数
         */
        private Info info;
        /**
         * etermface client
         */
        private EtermUfisClient client;
        /**
         * 下一个状态
         */
        private State nextState;
        /**
         * 当前状态
         */
        private State currentState;
        /**
         * 出错时的状态
         */
        private State errorState;
        /**
         * 错误信息
         */
        private String errorMsg;

        /**
         * 航班所对应的行号
         */
        private List<Integer> flightNoS = Lists.newArrayList();
        /**
         * 出票时限所对应的行号
         */
        private Integer tlNo;

        private String sfcNo;
        /**
         * 失败的次数
         */
        private int times;

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public EtermUfisClient getClient() {
            return client;
        }

        public void setClient(EtermUfisClient client) {
            this.client = client;
        }

        public State getNextState() {
            return nextState;
        }

        public void setNextState(State nextState) {
            this.nextState = nextState;
        }

        public State getCurrentState() {
            return currentState;
        }

        public void setCurrentState(State currentState) {
            this.currentState = currentState;
        }

        public List<Integer> getFlightNoS() {
            return flightNoS;
        }

        public void setFlightNoS(List<Integer> flightNoS) {
            this.flightNoS = flightNoS;
        }

        public Integer getTlNo() {
            return tlNo;
        }

        public void setTlNo(Integer tlNo) {
            this.tlNo = tlNo;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public State getErrorState() {
            return errorState;
        }

        public void setErrorState(State errorState) {
            this.errorState = errorState;
        }

        public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public String getSfcNo() {
            return sfcNo;
        }

        public void setSfcNo(String sfcNo) {
            this.sfcNo = sfcNo;
        }
    }

    private class Info {
        //pnr
        private String pnr;
        //office号
        private String officeId;
        //运价基础
        private String priceBase;
        //价格
        private String price;
        //打印机号
        private String printId;
        //如果发生异常，执行的最大次数，如请求超时
        private int maxTimes;

        public String getPnr() {
            return pnr;
        }

        public void setPnr(String pnr) {
            this.pnr = pnr;
        }

        public String getOfficeId() {
            return officeId;
        }

        public void setOfficeId(String officeId) {
            this.officeId = officeId;
        }


        public String getPriceBase() {
            return priceBase;
        }

        public void setPriceBase(String priceBase) {
            this.priceBase = priceBase;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrintId() {
            return printId;
        }

        public void setPrintId(String printId) {
            this.printId = printId;
        }

        public int getMaxTimes() {
            return maxTimes;
        }

        public void setMaxTimes(int maxTimes) {
            this.maxTimes = maxTimes;
        }
    }
}

  