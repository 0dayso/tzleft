package com.travelzen.etermface.web.jmx;

import com.travelzen.framework.quality.jmx.HealthLevel;
import com.travelzen.framework.quality.jmx.IHealthProvider;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/12
 * Time:下午1:33
 * <p/>
 * Description:
 */
@Component
public class HealthProvider implements IHealthProvider {

    @Override
    public HealthLevel getHealthLevel() {
        return HealthLevel.HEALTHY;
    }

    @Override
    public String getHealthInfo() {
        return "eterm web is healthy!";
    }

}

