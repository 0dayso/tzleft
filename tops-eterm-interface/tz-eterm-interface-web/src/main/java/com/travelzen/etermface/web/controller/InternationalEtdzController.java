package com.travelzen.etermface.web.controller;

import com.google.common.collect.Lists;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.common.pojo.EtdzResponse;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.util.EtermErrorCode;
import com.travelzen.framework.core.common.ReturnClass;
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
 * Date:15/1/20
 * Time:下午4:58
 * <p/>
 * Description:
 * <p/>
 * 国际自动出票
 */
@Controller
public class InternationalEtdzController {
    private static Logger logger = LoggerFactory.getLogger(InternationalEtdzController.class);


    /**
     * @param request
     * @param pnr      需要出票的PNR
     * @param officeId 出票的offcie号
     * @param printId  打印架终端号
     * @param airways  承运航司
     * @param price  价格
     * @return
     */
//    @RequestMapping(value = "/inetdz", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String execute(HttpServletRequest request,
                          @RequestParam String pnr,
                          @RequestParam String officeId,
                          @RequestParam String printId,
                          @RequestParam String airways,
                          @RequestParam String price) {
        logger.info("/inetdz接口请求参数:pnr={},officeId={},printId={},airways={}, price={}", pnr, officeId, printId, airways, price);
        EtdzResponse etdzResponse = null;
        if (pnr == null || officeId == null || printId == null || airways == null || price==null) {
        	etdzResponse = new EtdzResponse(false, "参数不合法,请检查请求参数");
        } else {
            Info info = new Info();
            info.setPnr(pnr);
            info.setOfficeId(officeId);
            info.setPrintId(printId);
            info.setMaxTimes(2);
            String[] aws = airways.split(",");
            List<String> list = Lists.newArrayList(aws);
            info.setAirways(list);
            info.setPrice(price);
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


    //运价行所对应正则表达式
    private static Pattern pricePattern = Pattern.compile("^(\\d+) .+? (\\d+) CNY.+");
    //出票时限对应的正则表达式
    private static Pattern tlPattern = Pattern.compile("^(\\d+).TL/.+");

    public enum States implements State {
        Begin {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Begin);
                try {
                    EtermWebClient etermWebClient = new EtermWebClient();
                    etermWebClient.connect(context.getInfo().getOfficeId());
                    context.setEtermWebClient(etermWebClient);
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
                context.getEtermWebClient().close();
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
                context.getEtermWebClient().close();
                logger.info("成功:状态机状态->Success,作用->EtermWebClient关闭链接,PNR={}", context.getInfo().getPnr());
                logger.info("-----------------------");
                return false;
            }
        },
        Pf {
            //特别注意，rr执行前一定要留在第一页，否者会出现ELE NBR
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Pf);
                try {
                    ReturnClass<String> returnClass = context.etermWebClient.rawExecuteCmd("pf", true);
                    if (returnClass.isSuccess()) {
                        String returnValue = returnClass.getObject().replaceAll("\r", "\n");
                        logger.info("状态机状态->Pf,执行结果:\n{}", returnValue);

                        context.setNextState(Xe);
                    } else {
                        logger.warn("PF请求失败");
                        context.setErrorMsg("PF请求失败");
                        context.setNextState(Counter);
                    }
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Rt {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Rt);
                try {
                    ReturnClass<String> rtReturn = context.getEtermWebClient().getRT(context.getInfo().getPnr(), true);
                    if (rtReturn.isSuccess()) {
                        String rtReturnObject = rtReturn.getObject();
                        String returnValue = rtReturnObject.replaceAll("\r", "\n");
                        logger.info("状态机状态->Rt,执行结果:\n{}", returnValue);

                        logger.info("成功:状态机状态->Rt,作用->获取PNR类容,PNR={}", context.getInfo().getPnr());
                        context.setNextState(Qte);
                    } else {
                        context.setNextState(Counter);
                        logger.warn("RT请求失败");
                        context.setErrorMsg("RT请求失败");
                    }
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Qte {
            //特别注意，rr执行前一定要留在第一页，否者会出现ELE NBR
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Qte);
                try {
                    String result = context.etermWebClient.getQTE(context.getInfo().getAirways(), "");
                    String returnValue = result.replaceAll("\r", "\n");
                    logger.info("状态机状态->Qte,执行结果:\n{}", returnValue);

                    if (returnValue.contains("NO FARES") || returnValue.contains("无适用的运价")) {
                        logger.warn("PNR中价格不存在");
                        context.setErrorMsg("PNR中价格不存在");
                        context.setNextState(Error);
                    } else {
                        String[] values = returnValue.split("\n");
                        for (String value : values) {
                            Matcher matcher1 = pricePattern.matcher(value);
                            if (matcher1.find()) {
                            	String number = matcher1.group(1);
                            	String price = matcher1.group(2);
                                // 只取和传进来的价格相匹配的报价序号
                                if (price.equals(context.getInfo().getPrice())) {
                                	logger.info("QTE价格匹配成功：{}, QTE价格{}==传入价格{}", number, price, context.getInfo().getPrice());
                                	context.getPriceNo().add(Integer.valueOf(number));
                                }
                            }
                        }
                        if (context.getPriceNo().size() > 0) {
                            context.setNextState(Xs_Fsq);
                            logger.info("成功:状态机状态->Qte,作用->获取运价,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                        } else {
                            context.setNextState(Counter);
                            logger.info("失败:状态机状态->Qte,作用->获取运价,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                            context.setErrorMsg("执行Qte,获取运价出错,PNR=" + context.getInfo().getPnr() + ",PrintId=" + context.getInfo().getPrintId());
                        }
                    }
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Xs_Fsq {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Xs_Fsq);
                try {
                    String result = context.getEtermWebClient().getXS_FSQ(context.getPriceNo().get(context.getPriceNo().size() - 1));
                    String returnValue = result.replaceAll("\r", "\n");
                    logger.info("状态机状态->Xs_Fsq,执行结果:\n{}", returnValue);

                    context.setNextState(Dfsq);
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Dfsq {
            public boolean process(Context context) {
                logger.info("************************");
                context.setCurrentState(Dfsq);
                try {
                    String result = context.getEtermWebClient().getDFSQ("A");
                    String returnValue = result.replaceAll("\r", "\n");
                    logger.info("状态机状态->Xs_Fsq,执行结果:\n{}", returnValue);

                    if (returnValue.contains("FC") && returnValue.contains("FN") && returnValue.contains("EI")) {
                        String[] lines = returnValue.split("\n");
                        for (String line : lines) {
                            Matcher matcher = tlPattern.matcher(line.trim());
                            if (matcher.find()) {
                                context.setTlNo(Integer.valueOf(matcher.group(1)));
                                break;
                            }
                        }
                        if (context.getTlNo() > 0) {
                            context.setNextState(Xe);
                            logger.info("成功:状态机状态->Dfsq,作用->存储操作,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                        } else {
                            context.setNextState(Error);
                            logger.warn("失败:状态机状态->Dfsq,作用->存储操作,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                            context.setErrorMsg("执行Dfsq,存储操作出错,PNR=" + context.getInfo().getPnr() + ",PrintId={}" + context.getInfo().getPrintId());
                        }
                    } else {
                        context.setNextState(Error);
                        logger.warn("PNR已出过票(存储的结果项中不存在FC/FN/EI)");
                        context.setErrorMsg("PNR已出过票");
                    }
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
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
                    String returnValue = context.etermWebClient.getXE(context.getTlNo()).replaceAll("\r", "\n");
                    logger.info("状态机状态->Xe,执行结果:\n{}", returnValue);

                    boolean isXeSuccess = true;
                    String[] lines = returnValue.split("\n");
                    for (String line : lines) {
                        Matcher matcher = tlPattern.matcher(line.trim());
                        if (matcher.find()) {
                            isXeSuccess = false;
                            break;
                        }
                    }
                    if (returnValue.contains(EtermErrorCode.ELE_NBR.getCode())) {
                        context.setCurrentState(Xe);
                        context.setNextState(Pf);
                        isXeSuccess = false;
                    }

                    if (isXeSuccess) {
                        logger.warn("成功:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
                        context.setNextState(Etdz);
                    } else {
                        logger.warn("失败:状态机状态->Xe,作用->删除出票时效,PNR={}", context.getInfo().getPnr());
                        context.setErrorMsg("执行Xe,删除出票时效出错,PNR=" + context.getInfo().getPnr());
                    }

                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
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
                    ReturnClass<String> returnClass = context.getEtermWebClient().rawExecuteCmd("ETDZ " + context.getInfo().getPrintId(), true);
                    if (returnClass.isSuccess()) {
                        String returnValue = returnClass.getObject().replaceAll("\r", "\n");
                        logger.info("状态机状态->Etdz,执行结果:\n{}", returnValue);

                        boolean isSuccess = false;
                        if (returnClass.getObject().contains("CNY") && returnClass.getObject().contains(context.getInfo().getPnr())) {
                            String[] lines = returnValue.split("\n");
                            if (lines.length == 2) {
                                isSuccess = true;
                            }
                        } else if (returnClass.getObject().contains("NO PNR")) {
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
                                context.setNextState(Counter);
                            }

                            logger.info("失败:状态机状态->Etdz,作用->打印机票,PNR={},PrintId={}", context.getInfo().getPnr(), context.getInfo().getPrintId());
                            context.setErrorMsg("执行Etdz,打印机票出错,PNR=" + context.getInfo().getPnr() + ",PrintId=" + context.getInfo().getPrintId());
                        }
                    } else {
                        logger.warn("ETDZ请求失败");
                        context.setErrorMsg("ETDZ请求失败");
                        context.setNextState(Counter);
                    }
                } catch (SessionExpireException e) {
                    context.setNextState(Error);
                    logger.warn("Session超时,{}", e);
                    context.setErrorMsg("Session超时," + e);
                }
                logger.info("-----------------------");
                return true;
            }
        },
        Counter {
            public boolean process(Context context) {
                logger.info("************************");
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
        private EtermWebClient etermWebClient;
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
         * 运价行号
         */
        private List<Integer> priceNo = Lists.newArrayList();
        /**
         * 出票时限所对应的行号
         */
        private Integer tlNo;
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

        public EtermWebClient getEtermWebClient() {
            return etermWebClient;
        }

        public void setEtermWebClient(EtermWebClient etermWebClient) {
            this.etermWebClient = etermWebClient;
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

        public List<Integer> getPriceNo() {
            return priceNo;
        }

        public void setPriceNo(List<Integer> priceNo) {
            this.priceNo = priceNo;
        }

        public Integer getTlNo() {
            return tlNo;
        }

        public void setTlNo(Integer tlNo) {
            this.tlNo = tlNo;
        }
    }

    private class Info {
        //pnr
        private String pnr;
        //office号
        private String officeId;
        //打印机号
        private String printId;
        //如果发生异常，执行的最大次数，如请求超时
        private int maxTimes;
        //出票航司
        private List<String> airways;
        //价格
        private String price;

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

        public List<String> getAirways() {
            return airways;
        }

        public void setAirways(List<String> airways) {
            this.airways = airways;
        }

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		@Override
		public String toString() {
			return "Info [pnr=" + pnr + ", officeId=" + officeId + ", printId="
					+ printId + ", maxTimes=" + maxTimes + ", airways="
					+ airways + ", price=" + price + "]";
		}
        
    }
}
