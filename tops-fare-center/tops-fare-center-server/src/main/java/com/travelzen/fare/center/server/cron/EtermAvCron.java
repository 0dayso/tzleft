package com.travelzen.fare.center.server.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EtermAv定时任务类
 * <p>
 * @author yiming.yan
 * @Date Nov 25, 2015
 */
@Component("etermAvCron")
public class EtermAvCron {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EtermAvCron.class);
	
	public void execute() {
		LOGGER.info("开始获取Eterm AV数据...");
		
		System.out.println("foobar");
		
		LOGGER.info("Eterm AV数据获取完成。");
	}
	
	public static void main(String[] args) {
		EtermAvCron y = new EtermAvCron();
		y.execute();
	}

}
