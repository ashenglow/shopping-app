package test.shop.infrastructure.monitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AlertService {
    private final Map<String, AlertThreshold> thresholds = new ConcurrentHashMap<>();

    public void checkThresholds(String methodName, long executionTime, QueryStats stats){
        AlertThreshold threshold = thresholds.get(methodName);
        if(threshold != null){
            if(executionTime > threshold.getSlowQueryThreshold()){
                log.warn("Slow query detected: {} took {}ms (threshold: {}ms)",
                        methodName, executionTime, threshold.getSlowQueryThreshold());
            }

            if(stats.getAverageTime() > threshold.getAvgTimeThreshold()){
                log.warn("High average time detected: {} avg is {}ms (threshold: {}ms)",
                        methodName, stats.getAverageTime(), threshold.getAvgTimeThreshold());
            }
        }
    }

    public void setThresholds(String methodName, long slowQueryThreshold, long avgTimeThreshold){
        thresholds.put(methodName, new AlertThreshold(slowQueryThreshold, avgTimeThreshold));
    }
}
