package com.travelzen.etermface.client.analysis.fare;

import com.travelzen.etermface.common.pojo.fare.NfdAllAnalysisResult;
import com.travelzen.etermface.common.pojo.fare.NfdFareResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/13
 * Time:下午2:26
 * <p/>
 * Description:
 * 将数据中心中半结构化数据变成结构化的数据
 */
public class NfdAllAnalysis {
    private static Logger logger = LoggerFactory.getLogger(NfdAllAnalysis.class);

    public static NfdAllAnalysisResult analyzeNfdInfo(NfdFareResponse.NfdInfo nfdInfo) {
        NfdAllAnalysisResult nfdAllAnalysisResult = new NfdAllAnalysisResult();
        analyzeNfdStr(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn01Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn02Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn04Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn05Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn06Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn08Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn09Str(nfdInfo, nfdAllAnalysisResult);
        analyzeNfn11Str(nfdInfo, nfdAllAnalysisResult);
        return nfdAllAnalysisResult;
    }

    private static void analyzeNfdStr(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        try {
            logger.info("开始解析NfdStr");
            NfdStrAnalysis.parse(nfdInfo.getNfdStr(), nfdAllAnalysisResult);
        } catch (Exception e) {
            logger.warn("NfdStr解析出现异常", e);
        }
    }

    private static void analyzeNfn01Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn01Str() != null) {
            logger.info("开始解析Nfn01Str");
            Nfn01Parser.parse(nfdInfo.getNfn01Str(),nfdAllAnalysisResult);
        }
    }

    private static void analyzeNfn02Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn02Str() != null) {
            logger.info("开始解析Nfn02Str");
        }
    }

    private static void analyzeNfn04Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn04Str() != null) {
            logger.info("开始解析Nfn04Str");
        }
    }

    private static void analyzeNfn05Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn05Str() != null) {
            logger.info("开始解析Nfn05Str");
        }
    }

    private static void analyzeNfn06Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn06Str() != null) {
            logger.info("开始解析Nfn06Str");
        }
    }

    private static void analyzeNfn08Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn08Str() != null) {
            logger.info("开始解析Nfn08Str");
        }
    }

    private static void analyzeNfn09Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn09Str() != null) {
            logger.info("开始解析Nfn09Str");
        }
    }

    private static void analyzeNfn11Str(NfdFareResponse.NfdInfo nfdInfo, NfdAllAnalysisResult nfdAllAnalysisResult) {
        if (nfdInfo.getNfn11Str() != null) {
            logger.info("开始解析Nfn11Str");
        }
    }
}
