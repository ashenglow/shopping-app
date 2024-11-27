package test.shop.infrastructure.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;
import test.shop.infrastructure.monitoring.model.dashboard.MonitoringDashboardData;
import test.shop.infrastructure.monitoring.model.dashboard.response.MethodMetricsData;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodPerformance;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodStats;
import test.shop.infrastructure.monitoring.model.dashboard.summary.PerformanceSummary;
import test.shop.infrastructure.monitoring.model.dashboard.summary.SlowQuerySummary;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.metrics.OperationMetric;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricsAggregationService {
    private final QueryPerformanceMonitor monitor;

    public MonitoringDashboardData aggregateMetrics(){
        Map<String, QueryStats> stats = monitor.getQueryStats();
        return MonitoringDashboardData.builder()
                .summary(createSummary(stats))
                .timeSeriesData(createTimeSeriesData(stats))
                .methodStats(createMethodStats(stats))
                .build();
    }

    public MethodMetricsData getMethodMetrics(String methodName) {
        QueryStats stats = monitor.getQueryStats().get(methodName);
        if(stats == null) {
            log.debug("No stats found for method: {}", methodName);
            return null;
        }

        log.info("Stats for {}: totalQueries={}, avgTime={}, slowQueries={}",
                methodName,
                stats.getTotalQueries().get(),
                stats.getAverageTime(),
                stats.getSlowQueries().get());

        List<OperationMetric> operationBreakdown = buildOperationBreakdown(stats);

        return MethodMetricsData.builder()
                .methodName(methodName)
                .averageTime(stats.getAverageTime())
                .totalQueries(stats.getTotalQueries().get())
                .slowQueries(stats.getSlowQueries().get())
                .recentSlowQueries(stats.getRecentSlowQueries())
                .timeSeriesData(createMethodTimeSeriesData(stats))
                .operationBreakdown(operationBreakdown)
                .build();
    }

    private List<OperationMetric> buildOperationBreakdown(QueryStats stats) {
        Map<String, List<QueryExecutionContext>> executionByOperation = stats.getQueryHistory().stream()
                .collect(Collectors.groupingBy(
                        context -> context.getMetadata().getOrDefault("operation", "unknown").toString()
                ));
    return executionByOperation.entrySet().stream()
            .map(entry -> {
                List<QueryExecutionContext> executions = entry.getValue();
                double avgTime = executions.stream()
                        .mapToLong(QueryExecutionContext::getExecutionTime)
                        .average()
                        .orElse(0.0);
                return OperationMetric.builder()
                        .operationName(entry.getKey())
                        .averageExecutionTime(avgTime)
                        .executionCount(executions.size())
                        .recentExecutions(executions.subList(
                                Math.max(0, executions.size() - 10),
                                executions.size()
                        ))
                        .build();

            })
            .collect(Collectors.toList());

    }
    public List<SlowQueryInfo> getMethodSlowQueries(String methodName) {
        QueryStats stats = monitor.getQueryStats().get(methodName);
        return  stats != null ? stats.getRecentSlowQueries() : Collections.emptyList();
    }


    public SlowQuerySummary createSlowQuerySummary(String methodName)
    {
        List<SlowQueryInfo> slowQueries = getMethodSlowQueries(methodName);

        if(slowQueries == null || slowQueries.isEmpty()) {
        return SlowQuerySummary.builder()
                .totalSlowQueries(0)
                .averageSlowQueryTime(0)
                .maxSlowQueryTime(0)
                .build();
    }
        return SlowQuerySummary.builder()
                .totalSlowQueries(slowQueries.size())
                .averageSlowQueryTime(slowQueries.stream()
                        .mapToLong(SlowQueryInfo::getExecutionTime)
                        .average()
                        .orElse(0))
                .maxSlowQueryTime(slowQueries.stream()
                        .mapToLong(SlowQueryInfo::getExecutionTime)
                        .max()
                        .orElse(0))
                .mostRecentSlowQuery(slowQueries.stream()
                        .map(SlowQueryInfo::getTimeStamp)
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .build();
    }

    private PerformanceSummary createSummary(Map<String, QueryStats> stats) {
        return PerformanceSummary.builder()
                .totalMethods(stats.size())
                .totalQueries(stats.values().stream()
                        .mapToLong(s -> s.getTotalQueries().get())
                        .sum())
                .totalSlowQueries(stats.values().stream()
                        .mapToLong(s -> s.getSlowQueries().get())
                        .sum())
                .averageResponseTime(stats.values().stream()
                        .mapToDouble(QueryStats::getAverageTime)
                        .average()
                        .orElse(0.0))
                .build();
    }

    private List<TimeSeriesPoint> createTimeSeriesData(Map<String, QueryStats> stats){
        // Implementation depends on how you store historical data
        // This is a placeholder that you'll need to implement
        return stats.values().stream()
                .flatMap(s -> s.getTimeSeriesData().stream())
                .collect(Collectors.toList());
    }

    private MethodStats createMethodStats(Map<String, QueryStats> stats){
        return MethodStats.builder()
                .methodPerformance(stats.entrySet().stream()
                        .map(entry -> MethodPerformance.builder()
                                .methodName(entry.getKey())
                                .averageTime(entry.getValue().getAverageTime())
                                .totalQueries(entry.getValue().getTotalQueries().get())
                                .slowQueries(entry.getValue().getSlowQueries().get())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private List<TimeSeriesPoint> createMethodTimeSeriesData(QueryStats stats){
        return stats != null ? stats.getTimeSeriesData() : Collections.emptyList();
    }

}
