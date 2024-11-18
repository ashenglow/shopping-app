package test.shop.infrastructure.monitoring.web;

import jdk.jfr.Threshold;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;
import test.shop.infrastructure.monitoring.model.dashboard.MonitoringDashboardData;
import test.shop.infrastructure.monitoring.model.dashboard.response.MethodMetricsData;
import test.shop.infrastructure.monitoring.model.dashboard.response.SlowQueriesResponse;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodPerformance;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodStats;
import test.shop.infrastructure.monitoring.model.dashboard.summary.PerformanceSummary;
import test.shop.infrastructure.monitoring.model.dashboard.summary.SlowQuerySummary;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.metrics.QueryMetrics;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;
import test.shop.infrastructure.monitoring.service.AlertService;
import test.shop.infrastructure.monitoring.service.MetricsAggregationService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/monitoring")
@RequiredArgsConstructor
public class MonitoringController {
    private final MetricsAggregationService metricService;
    private final QueryPerformanceMonitor queryPerformanceMonitor;
    private final AlertService alertService;

    @GetMapping("/metrics")
    public MonitoringDashboardData getMetrics() {
        return metricService.aggregateMetrics();
    }

    @GetMapping("/metrics/{methodName}")
    public MethodMetricsData getMethodMetrics(@PathVariable String methodName){
        return metricService.getMethodMetrics(methodName);
    }

    //for order-specific metrics, use method name pattern matching
    @GetMapping("/metrics/orders")
    public List<MethodMetricsData> getOrderMetrics() {
        return queryPerformanceMonitor.getQueryStats().keySet().stream()
                .filter(method -> method.toLowerCase().contains("order"))
                .map(metricService::getMethodMetrics)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    @GetMapping("/slow-queries/{methodName}")
    public SlowQueriesResponse getSlowQueries(
            @PathVariable String methodName
    ) {
        return SlowQueriesResponse.builder()
                .methodName(methodName)
                .slowQueries(metricService.getMethodSlowQueries(methodName))
                .summary(metricService.createSlowQuerySummary(methodName))
                .build();

    }
    @PostMapping("/thresholds/{methodName}")
    public void setThreshold(@PathVariable String methodName,
                             @RequestParam long slowQueryThreshold,
                             @RequestParam long avgTimeThreshold) {
        alertService.setThresholds(methodName, slowQueryThreshold, avgTimeThreshold);
    }



}
