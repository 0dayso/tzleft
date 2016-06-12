package com.travelzen.etermface.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.common.PNRDateFormat;
import com.travelzen.etermface.service.common.PnrRecordParser;
import com.travelzen.etermface.service.common.WriteFile;
import com.travelzen.etermface.service.entity.PatParams;
import com.travelzen.etermface.service.entity.PnrOpResult;
import com.travelzen.etermface.service.entity.SeatPrice;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.util.TZUtil;

/**
 * 国内报价，只有政府采购走eterm。
 * <p/>
 * 政府报价，如果时间太近，加30天重新获取
 */
public class PatPriceByUfisParser {
    /**
     * 取消pnr成功
     */
    private final static String CANCEL_SUCCESS = "PNR CANCELLED ";

    /**
     * 错误信息
     */
    private final static String ERROR_INFO = "请一次";

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 小时分钟
     */
    private final static SimpleDateFormat hs_sdf = new SimpleDateFormat("HHmm");

    /**
     * 十年，取儿童生日会用到
     */
    private final static long TEN_YEARS = 10 * 365 * 24 * 60 * 60 * 1000L;

    /**
     * 一小时
     */
    private final static long ONE_HOUR = 60 * 60 * 1000l;

    /**
     * 订座用的姓名
     */
    private final static List<String> NAMES = new ArrayList<String>();

    /**
     * 航空公司
     */
    private String airLine = null;

    private static Logger logger = LoggerFactory.getLogger(PatPriceByUfisParser.class);

    /**
     * 100姓名，随机取来生成pnr
     */
    static {
        String path = PatPriceByUfisParser.class.getResource("/").toString();
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        path = path + "/patconf/names.txt";

        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if ("".equals(line.trim())) {
                    break;
                }
                NAMES.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 获取PAT价格 流程说明： 1.先用
     *
     * @param pPatParams
     * @return
     * @throws SessionExpireException
     */
    public String patPrice(String pPatParams, String passengerType, boolean isGovern) {
        List<SeatPrice> result = new ArrayList<SeatPrice>();
        List<SeatPrice> adultPriceList = null;
        List<SeatPrice> childPriceList = null;
        List<SeatPrice> goverPriceList = null;

        List<PatParams> patParamsList = PatParams.convertFromXML(pPatParams);

        if (null == patParamsList || patParamsList.size() == 0) {
            return null;
        }

        List<String> adultCommandList = convertToBookCommand(patParamsList, false);
        List<String> childCommandList = convertToBookCommand(patParamsList, true);

        EtermUfisClient client = null;
        if (isGovern) {
            if (null != adultCommandList && adultCommandList.size() > 0) {
                try {
                	client = new EtermUfisClient();
                    adultPriceList = this.patGoverPrice(client, adultCommandList);
                } catch (UfisException e) {
                    logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                    // 异常重新获取ETERM客户端，并重试
                    client.close();
                    try {
                    	client = new EtermUfisClient();
                        adultPriceList = this.patGoverPrice(client, adultCommandList);
                    } catch (UfisException e1) {
                        logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                        client.close();
                    }
                }

                if (null == adultPriceList || adultPriceList.size() == 0) {
                    adultCommandList = convertToBookCommandGov(patParamsList, false);
                    try {
                    	client = new EtermUfisClient();
                        adultPriceList = this.patGoverPrice(client, adultCommandList);
                    } catch (UfisException e) {
                        logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                        // 异常重新获取ETERM客户端，并重试
                        client.close();
                        try {
                        	client = new EtermUfisClient();
                            adultPriceList = this.patGoverPrice(client, adultCommandList);
                        } catch (UfisException e1) {
                            logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                            client.close();
                        }
                    }
                }
            }
        } else {
            if (StringUtils.isBlank(passengerType) || "ADT".equalsIgnoreCase(passengerType)) {
                if (null != adultCommandList && adultCommandList.size() > 0) {
                    try {
                    	client = new EtermUfisClient();
                        adultPriceList = this.patAdultPrice(client, adultCommandList);
                    } catch (UfisException e) {
                        logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                        // 异常重新获取ETERM客户端，并重试
                        client.close();
                        try {
                        	client = new EtermUfisClient();
                            adultPriceList = this.patAdultPrice(client, adultCommandList);
                        } catch (UfisException e1) {
                            logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                            client.close();
                        }
                    }

                    if (null == adultPriceList || adultPriceList.size() == 0) {
                        try {
                        	client = new EtermUfisClient();
                            adultPriceList = this.patAdultPriceByPnr(client, adultCommandList);
                        } catch (UfisException e) {
                            logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                            // 异常重新获取ETERM客户端，并重试
                            client.close();
                            try {
                            	client = new EtermUfisClient();
                                adultPriceList = this.patAdultPriceByPnr(client, adultCommandList);
                            } catch (UfisException e1) {
                                logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                                client.close();
                            }
                        }
                    }
                }
            }

            /**
             * 设置是按具体舱位进行pat的
             */
            setIsCabinCode(adultPriceList, true);


            if (StringUtils.isBlank(passengerType) || "CHD".equalsIgnoreCase(passengerType)) {
                // Y,F，C舱位去PAT儿童价格
                if (null == childPriceList || childPriceList.size() == 0) {
                    try {
                    	client = new EtermUfisClient();
                        childPriceList = this.patChildPrice(client, childCommandList);
                    } catch (UfisException e) {
                        logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                        // 异常重新获取ETERM客户端，并重试
                        client.close();
                        try {
                        	client = new EtermUfisClient();
                            childPriceList = this.patChildPrice(client, childCommandList);
                        } catch (UfisException e1) {
                            logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                            client.close();
                        }
                    }
                    if (null == childPriceList || childPriceList.size() == 0) {
                        try {
                        	client = new EtermUfisClient();
                            childPriceList = this.patChildPriceByPnr(client, childCommandList);
                        } catch (UfisException e) {
                            // 异常重新获取ETERM客户端，并重试
                            logger.error("UfisException err:{}", TZUtil.stringifyException(e));
                            client.close();
                            try {
                            	client = new EtermUfisClient();
                                childPriceList = this.patChildPriceByPnr(client, childCommandList);
                            } catch (UfisException e1) {
                                logger.error("UfisException err:{}", TZUtil.stringifyException(e1));
                                client.close();
                            }
                        }
                    }
                    setIsCabinCode(childPriceList, false);
                }
            }
        }

        //关闭链接
        if (null != client)
        	client.close();
        /**
         * 将成人价格放到结果中
         */
        if (null != adultPriceList && adultPriceList.size() > 0) {
            for (SeatPrice seatPrice : adultPriceList) {
                if (seatPrice.getPassengerType().equals("ADT")) {
                    result.add(seatPrice);
                }
            }
        }
        /**
         * 将儿童价格放到结果中
         */
        if (null != childPriceList && childPriceList.size() > 0) {
            for (SeatPrice seatPrice : childPriceList) {
                result.add(seatPrice);
            }
        }
        /**
         * 将婴儿价格放到结果中
         */
        if (null != adultPriceList && adultPriceList.size() > 0) {
            for (SeatPrice seatPrice : adultPriceList) {
                if (seatPrice.getPassengerType().equals("INF")) {
                    result.add(seatPrice);
                }
            }
        }
        return toXML(result);
    }

    /**
     * pat儿童价格
     *
     * @param pCommandList
     * @return
     * @throws SessionExpireException
     */
    private List<SeatPrice> patChildPrice(EtermUfisClient client, List<String> pCommandList) throws UfisException {

        List<SeatPrice> result = null;

        /**
         * 撤销上次操作(还原)
         */
        client.resume();

        /**
         * 一次全部SS完毕
         */
        StringBuffer command = new StringBuffer();
        for (String tmCommand : pCommandList) {
            command.append(tmCommand);
            command.append("\r");
        }

        String rtStr = client.execCmd(command.toString(), true);
        logger.info("command-->" + command.toString() + "\nretrun-->" + rtStr);
        if (rtStr.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY WITH \"AV\" AGAIN ")) {
            logger.info("无座停止获取价格");
            return null;
        }
        if (!rtStr.contains(ERROR_INFO)) {

            /**
             * 添加乘客名
             */
            String nameCommand = "NM1" + getName() + "CHD";
            rtStr = client.execCmd(nameCommand, true);
            logger.info("command-->" + nameCommand + "\nreturn-->" + rtStr);

            /**
             * 补充乘客信息
             */
            String ssrCommand = getSSRCommand();
            rtStr = client.execCmd(ssrCommand, true);
            logger.info("command-->" + ssrCommand + "\nreturn-->" + rtStr);
            if (rtStr.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY WITH \"AV\" AGAIN ")) {
                logger.info("无座停止获取价格");
                return null;
            }
            String patACHStr = client.execCmd("PAT:A*CH");
            logger.info("command--> PAT:A*CH\nreturn-->" + patACHStr);
            result = SeatPrice.parseSeatPrice(patACHStr, "CHD");

            /**
             * 撤销订座(还原)，最多三次即可
             */
            for (int count = 0; count < 3; count++) {
                String str = client.execCmd("I", false);
                logger.info("command--> I\nreturn-->" + str);
                if (null == str) {
                    continue;
                }
                if ("NO PNR".equals(str.trim())) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * pat成人婴儿价格
     *
     * @param pCommandList
     * @return
     * @throws SessionExpireException
     */
    private List<SeatPrice> patAdultPrice(EtermUfisClient client, List<String> pCommandList) throws UfisException {
        List<SeatPrice> result = new ArrayList<SeatPrice>();
        List<SeatPrice> priceList = null;

        /**
         * 撤销上次操作(还原)
         */
        client.resume();

        /**
         * 一次全部SS完毕
         */
        StringBuffer command = new StringBuffer();
        for (String tmCommand : pCommandList) {
            command.append(tmCommand);
            command.append("\r");
        }

        String rtStr = client.execCmd(command.toString(), true);
        logger.info("command-->" + command.toString() + "\nretrun-->" + rtStr);
        if (rtStr.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY WITH \"AV\" AGAIN ")) {
            logger.info("无座停止获取价格");
            return null;
        }
        if (!rtStr.contains(ERROR_INFO)) {
            String patAStr = client.execCmd("PAT:A");
            logger.info("command--> PAT:A\nreturn-->" + patAStr);
            priceList = SeatPrice.parseSeatPrice(patAStr, "ADT");
            if (null != priceList) {
                result.addAll(priceList);
            }

            String patAINStr = client.execCmd("PAT:A*IN");
            logger.info("command--> PAT:A*IN\nreturn-->" + patAINStr);
            priceList = SeatPrice.parseSeatPrice(patAINStr, "INF");
            if (null != priceList) {
                result.addAll(priceList);
            }
        }

        return result;
    }

    /**
     * 生成pnr后，再PAT价格，最后取消PNR(儿童)
     *
     * @param pCommandList
     * @return
     * @throws SessionExpireException
     */
    private List<SeatPrice> patChildPriceByPnr(EtermUfisClient client, List<String> pCommandList) throws UfisException {

        List<SeatPrice> result = new ArrayList<SeatPrice>();
        List<SeatPrice> priceList = null;

        /**
         * 撤销上次操作(还原)
         */
        client.resume();

        StringBuffer command = new StringBuffer();
        for (String tmCommand : pCommandList) {
            command.append(tmCommand);
            command.append("\r");
        }

        String ssrCommand = getSSRCommand();

        /**
         * 加上出票时限
         */
        command.append(getTKTLCommand());
        command.append("\r");

        command.append("CT56788888");
        command.append("\r");
        command.append("NM1");
        command.append(getName());
        command.append("CHD");
        command.append("\r");
        command.append(ssrCommand);
        command.append("\r");
        command.append("\\");
        String rtStr = client.execCmd(command.toString(), true);

        logger.info("command--> " + command.toString() + "\nreturn-->" + rtStr);
        String pnr = PnrRecordParser.getPNRFromSSReturn(rtStr);

        if (null == pnr) {
            logger.error(rtStr);
            return null;
        }

        /**
         * 写log
         */
        WriteFile.write(pnr);

        /**
         * PAT价格
         */
        client.execRt(pnr, true);

        String patStr = client.execCmd("PAT:A*CH");
        logger.info("command--> PAT:A*CH\nreturn-->" + patStr);
        priceList = SeatPrice.parseSeatPrice(patStr, "CHD");
        if (null != priceList) {
            result.addAll(priceList);
        }

        /**
         * 取消PNR
         */
        rtStr = client.cancelPnr();

        String cancelSuccess = CANCEL_SUCCESS + pnr;
        if (!cancelSuccess.equalsIgnoreCase(rtStr.trim())) {
            PnrOpResult pnrOpResult = new CancelPNRParser().cancelPnrByUfis(pnr, false, null, false);
            if (!pnrOpResult.getReturnCode().equals(ReturnCode.SUCCESS.getErrorCode())) {
                logger.error(pnr + "取消失败\n" + rtStr);
                WriteFile.write("取消失败" + pnr);
            }
        }

        return result;
    }

    /**
     * 生成pnr后，再PAT价格，最后取消PNR(成人和婴儿)
     *
     * @param pCommandList
     * @return
     * @throws SessionExpireException
     */
    private List<SeatPrice> patAdultPriceByPnr(EtermUfisClient client, List<String> pCommandList) throws UfisException {

        List<SeatPrice> result = new ArrayList<SeatPrice>();
        List<SeatPrice> priceList = null;

        /**
         * 撤销上次操作(还原)
         */
        client.resume();

        StringBuffer command = new StringBuffer();
        for (String tmCommand : pCommandList) {
            command.append(tmCommand);
            command.append("\r");
        }

        /**
         * 加上出票时限
         */
        command.append(getTKTLCommand());
        command.append("\r");

        command.append("CT56788888");
        command.append("\r");
        command.append("NM1");
        command.append(getName());
        command.append("\r");
        command.append("\\");
        String rtStr = client.execCmd(command.toString(), true);

        logger.info("command--> " + command.toString() + "\nreturn-->" + rtStr);

        String pnr = PnrRecordParser.getPNRFromSSReturn(rtStr);

        if (null == pnr) {
            logger.error(rtStr);
            return null;
        }

        /**
         * 写log
         */
        WriteFile.write(pnr);
        /**
         * PAT价格
         */
        client.execRt(pnr, true);

        String patAStr = client.execCmd("PAT:A");
        logger.info("command--> PAT:A\nreturn-->" + patAStr);
        priceList = SeatPrice.parseSeatPrice(patAStr, "ADT");
        if (null != priceList) {
            result.addAll(priceList);
        }

        String patAINStrlvPatStr = client.execCmd("PAT:A*IN");
        logger.info("command--> PAT:A*IN\nreturn-->" + patAINStrlvPatStr);
        priceList = SeatPrice.parseSeatPrice(patAINStrlvPatStr, "INF");
        if (null != priceList) {
            result.addAll(priceList);
        }

        /**
         * 取消PNR
         */
        rtStr = client.cancelPnr();

        String cancelSuccess = CANCEL_SUCCESS + pnr;
        if (!cancelSuccess.equalsIgnoreCase(rtStr.trim())) {
            PnrOpResult pnrOpResult = new CancelPNRParser().cancelPnrByUfis(pnr, false, null, false);
            if (!pnrOpResult.getReturnCode().equals(ReturnCode.SUCCESS.getErrorCode())) {
                logger.error(pnr + "取消失败\n" + rtStr);
                WriteFile.write("取消失败" + pnr);
            }
        }

        return result;
    }

    private String toXML(List<SeatPrice> pList) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.alias("PatResult", List.class);
        xstream.processAnnotations(SeatPrice.class);
        return xstream.toXML(pList);
    }

    /**
     * 将输入的参数列表转化成对应的SS命令
     *
     * @param pPatParamsList
     * @param pIsChild
     * @return
     */
    private List<String> convertToBookCommand(List<PatParams> pPatParamsList, boolean pIsChild) {
        List<String> list = new ArrayList<String>();
        String lvCommand = null;
        for (PatParams tmPatParams : pPatParamsList) {
            lvCommand = convertToBookCommand(tmPatParams, pIsChild);
            if (null != lvCommand) {
                list.add(lvCommand);
            } else {
                return null;
            }
        }
        return list;
    }

    /**
     * 获取政府报价，将输入的参数列表转化成对应的SS命令
     *
     * @param pPatParamsList
     * @param pIsChild
     * @return
     */
    private List<String> convertToBookCommandGov(List<PatParams> pPatParamsList, boolean pIsChild) {
        List<String> list = new ArrayList<String>();
        String lvCommand = null;
        for (PatParams tmPatParams : pPatParamsList) {
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime dateTime = DateTime.parse(tmPatParams.getDepatureDate(), format);
            dateTime = dateTime.plusDays(7);
            tmPatParams.setDepatureDate(dateTime.toString("yyyy-MM-dd"));
            lvCommand = convertToBookCommand(tmPatParams, pIsChild);
            if (null != lvCommand) {
                list.add(lvCommand);
            } else {
                return null;
            }
        }
        return list;
    }

    private String convertToBookCommand(PatParams pPatParams, boolean pIsChild) {
        if (null == pPatParams) {
            return null;
        }
        if (null == pPatParams.getAirNo() || pPatParams.getAirNo().isEmpty()) {
            return null;
        }
        if (null == pPatParams.getSmallCabin() || pPatParams.getSmallCabin().isEmpty()) {
            return null;
        }
        if (null == pPatParams.getDepatureDate() || pPatParams.getDepatureDate().isEmpty()) {
            return null;
        }
        if (null == pPatParams.getFromAirPort() || pPatParams.getFromAirPort().isEmpty()) {
            return null;
        }
        if (null == pPatParams.getToAirPort() || pPatParams.getToAirPort().isEmpty()) {
            return null;
        }

        String date = PNRDateFormat.dayMonthFormat(pPatParams.getDepatureDate());
        if (null == date) {
            return null;
        }

        /**
         * 赋值给airLine,后面取SSR_COMMAND会用到
         */
        if (null == this.airLine) {
            this.airLine = pPatParams.getAirNo().substring(0, 2);
        }

        StringBuffer resultBuffer = new StringBuffer();
        resultBuffer.append("SS ");
        resultBuffer.append(pPatParams.getAirNo());
        resultBuffer.append(" ");
        if (pIsChild) {
            if (StringUtils.isBlank(pPatParams.getCabin())) {
                resultBuffer.append(pPatParams.getSmallCabin());
            } else {
                resultBuffer.append(pPatParams.getCabin());
            }
        } else {
            resultBuffer.append(pPatParams.getSmallCabin());
        }
        resultBuffer.append(" ");
        resultBuffer.append(date);
        resultBuffer.append(" ");
        resultBuffer.append(pPatParams.getFromAirPort());
        resultBuffer.append(pPatParams.getToAirPort());
        resultBuffer.append(" NN");
        resultBuffer.append(1);
        return resultBuffer.toString();
    }

    /**
     * 获取SSR命令 如:SSR CHLD MU HK1/01MAR06/P1
     */
    private String getSSRCommand() {
        StringBuffer commandBuffer = new StringBuffer("SSR CHLD ");
        commandBuffer.append(this.airLine);
        commandBuffer.append(" HK1/");
        commandBuffer.append(getChildBirthday());
        commandBuffer.append("/P1");

        return commandBuffer.toString();
    }

    /**
     * 获取出票时限命令
     *
     * @return 如：TKTL2000/24JUL/SHA255
     */
    private String getTKTLCommand() {
        Date date = new Date(System.currentTimeMillis() + ONE_HOUR);
        StringBuffer commandBuffer = new StringBuffer("TKTL");
        // 小时
        commandBuffer.append(hs_sdf.format(date));
        commandBuffer.append("/");
        String dateStr = sdf.format(date);
        commandBuffer.append(PNRDateFormat.dayMonthFormat(dateStr));
        commandBuffer.append("/SHA255");

        return commandBuffer.toString();
    }

    /**
     * 获取儿童生日 如：01MAR06 10年前的03月01日
     *
     * @return
     */
    private String getChildBirthday() {
        String dateStr = sdf.format(new Date(System.currentTimeMillis() - TEN_YEARS));
        String birthday = "01MAR" + dateStr.substring(2, 4);
        return birthday.toString();
    }

    /**
     * 随机返回一个乘客名称
     *
     * @return
     */
    private String getName() {
        int size = NAMES.size();
        if (size == 0) {
            NAMES.add("张秀兰");
            return NAMES.get(0);
        }

        int index = new Random().nextInt(size);
        return NAMES.get(index);
    }

    /**
     * 设置是按舱位PAT的价格，还是按舱位等级PAT的价格
     *
     * @param priceList
     * @param patByCabinCode
     */
    private void setIsCabinCode(List<SeatPrice> priceList, boolean patByCabinCode) {
        if (null == priceList || 0 == priceList.size()) {
            return;
        }
        for (SeatPrice price : priceList) {
            price.setPatByCabinCode(patByCabinCode);
        }
    }

    /**
     * pat政府采购成人价格
     *
     * @param pCommandList
     * @return
     * @throws SessionExpireException
     */
    private List<SeatPrice> patGoverPrice(EtermUfisClient client, List<String> pCommandList) throws UfisException {
        List<SeatPrice> result = new ArrayList<SeatPrice>();
        List<SeatPrice> priceList = null;

        /**
         * 撤销上次操作(还原)
         */
        client.resume();

        /**
         * 一次全部SS完毕
         */
        StringBuffer command = new StringBuffer();
        for (String tmCommand : pCommandList) {
            command.append(tmCommand);
            command.append("\r");
        }

        String rtStr = client.execCmd(command.toString(), false);
        logger.info("command-->" + command.toString() + "\nretrun-->" + rtStr);
//        if (rtStr.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY WITH \"AV\" AGAIN")) {
//            logger.info("无座停止获取价格");
//            return null;
//        }
        if (!rtStr.contains(ERROR_INFO)) {
            String patAStr = client.execCmd("PAT:A#CGP/CC");
            logger.info("command--> PAT:A#CGP/CC\nreturn-->" + patAStr);
            priceList = SeatPrice.parseSeatPrice(patAStr, "ADT");
            if (null != priceList) {
                List<SeatPrice> GoverPriceList = new ArrayList<>();
                for (SeatPrice seatPrice : priceList) {
                    //此处会获取非政府报价，政府报价的CabinNumber结尾为GP，所以需要过滤下
                    if (seatPrice.getCabinNumber().contains("GP")) {
                        GoverPriceList.add(seatPrice);
                    }
                }
                result.addAll(GoverPriceList);
            }
        }
        return result;
    }
}
