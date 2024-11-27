package test.shop.infrastructure.monitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test.shop.infrastructure.monitoring.model.alert.Alert;
import test.shop.infrastructure.monitoring.model.alert.AlertSeverity;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.alert.AlertType;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlertService {
    private final Map<String, AlertThreshold> thresholds = new ConcurrentHashMap<>();
    private final Queue<Alert> recentAlerts = new ConcurrentLinkedQueue<>();
    private static final int MAX_ALERTS = 100;

    public void checkThresholds(String methodName, long executionTime, QueryStats stats){
        AlertThreshold threshold = thresholds.get(methodName);
        if(threshold != null){
            if(executionTime > threshold.getSlowQueryThreshold()){
               Alert alert = createAlert(
                       AlertType.SLOW_QUERY,
                       methodName,
                       executionTime,
                       threshold.getSlowQueryThreshold()
               );
               addAlert(alert);
                log.warn("Slow query detected: {} took {}ms (threshold: {}ms)",
                        methodName, executionTime, threshold.getSlowQueryThreshold());
            }

            if(stats.getAverageTime() > threshold.getAvgTimeThreshold()){
                Alert alert = createAlert(
                        AlertType.HIGH_AVERAGE_TIME,
                        methodName,
                        stats.getAverageTime(),
                        threshold.getAvgTimeThreshold()
                );
                addAlert(alert);
                log.warn("High average time detected: {} avg is {}ms (threshold: {}ms)",
                        methodName, stats.getAverageTime(), threshold.getAvgTimeThreshold());
            }
        }
    }

    public void handleSlowQuery(QueryExecutionContext context, AlertThreshold threshold){
        //create alert with additional context
        Alert alert = Alert.builder()
                .type(AlertType.SLOW_QUERY)
                .methodName(context.getMethodKey())
                .severity(determineSeverity(context.getExecutionTime(), threshold))
                .metric(context.getExecutionTime())
                .threshold(threshold.getSlowQueryThreshold())
                .timestamp(context.getTimestamp())
                .details(buildAlertDetails(context))
                .build();

        addAlert(alert);
    }

    private String buildAlertDetails(QueryExecutionContext context){
        StringBuilder details = new StringBuilder();
        details.append("Execution time: ").append(context.getExecutionTime()).append("ms\n");

        // add relevant metadata
        context.getMetadata().forEach((key, value) -> {
            if (isRelevantMetadata(key)){
                details.append(key).append(": ").append(value).append("\n");
            }
        });
        if(context.isError()){
            details.append("Error occurred during execution\n");
        }
        return details.toString();
    }

    private boolean isRelevantMetadata(String key){
        return key.startsWith("param.") ||
                key.equals("operationType") ||
                key.equals("queryPattern");
    }
    private AlertSeverity determineSeverity(long executionTime, AlertThreshold threshold){
        double ratio = (double) executionTime / threshold.getSlowQueryThreshold();
        if(ratio >= 3.0) return AlertSeverity.CRITICAL;
        if(ratio >= 2.0) return AlertSeverity.ERROR;
        if(ratio >= 1.0) return AlertSeverity.WARNING;
        return AlertSeverity.INFO;
    }
    private void handleCriticalAlert(Alert alert, QueryExecutionContext context) {
        log.error("CRITICAL PERFORMANCE ALERT: {}\nDetails: {}",
                alert.getMethodName(), alert.getDetails());
    }

    private Alert createAlert(AlertType type, String methodName, double metric, double threshold){
        return Alert.builder()
                .type(type)
                .methodName(methodName)
                .severity(determineSeverity((long) metric, new AlertThreshold((long) threshold, 0)))
                .metric(metric)
                .threshold(threshold)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void addAlert(Alert alert){
        recentAlerts.offer(alert);
        while(recentAlerts.size() > MAX_ALERTS){
            recentAlerts.poll();
        }
    }
    public void setThresholds(String methodName, long slowQueryThreshold, long avgTimeThreshold){
        thresholds.put(methodName, new AlertThreshold(slowQueryThreshold, avgTimeThreshold));
    }

    public List<Alert> getRecentAlerts() {
        return new ArrayList<>(recentAlerts);
    }

    public List<Alert> getRecentAlertsByMethod(String methodName) {
        return recentAlerts.stream()
                .filter(alert -> alert.getMethodName().equals(methodName))
                .collect(Collectors.toList());
    }

}
