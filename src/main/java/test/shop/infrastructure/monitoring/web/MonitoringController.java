package test.shop.infrastructure.monitoring.web;

import jdk.jfr.Threshold;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import test.shop.domain.model.analytics.DailySalesStats;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;
import test.shop.infrastructure.monitoring.model.alert.Alert;
import test.shop.infrastructure.monitoring.model.dashboard.MonitoringDashboardData;
import test.shop.infrastructure.monitoring.model.dashboard.response.MethodMetricsData;
import test.shop.infrastructure.monitoring.model.dashboard.response.SlowQueriesResponse;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodPerformance;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodStats;
import test.shop.infrastructure.monitoring.model.dashboard.summary.PerformanceSummary;
import test.shop.infrastructure.monitoring.model.dashboard.summary.SlowQuerySummary;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.metrics.OrderFlowMonitor;
import test.shop.infrastructure.monitoring.model.metrics.QueryMetrics;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;
import test.shop.infrastructure.monitoring.service.AlertService;
import test.shop.infrastructure.monitoring.service.MetricsAggregationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {
    private final MetricsAggregationService metricService;
    private final QueryPerformanceMonitor queryPerformanceMonitor;
    private final OrderFlowMonitor orderFlowMonitor;
    private final AlertService alertService;
    private final DailySalesStatsRepository statsRepository;



    @GetMapping("/metrics")
    public MonitoringDashboardData getMetrics() {
        return metricService.aggregateMetrics();
    }

    @GetMapping("/metrics/{methodName}")
    public MethodMetricsData getMethodMetrics(@PathVariable String methodName){
        return metricService.getMethodMetrics(methodName);
    }
//
//    @GetMapping("/metrics/analytics")
//    public Map<String, Object> getAnalyticsMetrics() {
//        List<OrderFlowMonitor.OrderExecutionMetrics> executions =
//                orderFlowMonitor.getRecentExecutions();
//
//        Map<String, Object> metrics = new HashMap<>();
//        metrics.put("timestamp", LocalDateTime.now());
//        metrics.put("batchExecutions", executions.size());
//        // add today's sales stats if available
//        LocalDate today = LocalDate.now();
//        statsRepository.findByDate(today)
//                .ifPresent( stats -> {
//                    metrics.put("today", Map.of(
//                            "total", stats.getTotalOrders(),
//                            "revenue", stats.getTotalRevenue(),
//                            "averageValue", stats.getAverageOrderValue()
//                    ));
//                    metrics.put("categoryBreakdown", stats.getSalesByCategory());
//                });
//
//        // last 7 days stats
//        LocalDate weekAgo = today.minusDays(7);
//        List<DailySalesStats> weeklyStats = statsRepository.findStatsForRange(weekAgo, today);
//        metrics.put("weeklyStats", weeklyStats.stream()
//                .collect(Collectors.toMap(
//                        stats -> stats.getDate().toString(),
//                        DailySalesStats::getTotalRevenue
//                )));
//
//        // this month's total revenue
//        LocalDate monthStart = today.withDayOfMonth(1);
//        Integer monthlyRevenue = statsRepository.getTotalRevenueForDate(monthStart)
//                .orElse(0);
//        metrics.put("monthlyRevenue", monthlyRevenue);
//        return metrics;
//    }

    @GetMapping("/metrics/items")
    public List<MethodMetricsData> getItemMetrics() {
        Set<String> itemPatterns = new HashSet<>(Arrays.asList(
                "item", "product", "inventory"
        ));

        return getMetricsForPatterns(itemPatterns);
    }

    //for order-specific metrics, use method name pattern matching
//    @GetMapping("/metrics/orders")
//    public List<MethodMetricsData> getOrderMetrics() {
//
//        log.info("Fetching order metrics...");
//        Set<String> orderRelatedPatterns = new HashSet<>(Arrays.asList(
//                "order",         // Direct order methods
//                "delivery",      // Delivery creation/updates
//                "member",        // Member validation
//                "item",          // Item retrieval for orders
//                "createorder",   // Order creation method
//                "addorderitem",  // Order item addition
//                "createorderitem" // Order item creation
//        ));
//        List<MethodMetricsData> orderMetrics = queryPerformanceMonitor.getQueryStats().keySet().stream()
//                .filter(method -> orderRelatedPatterns.stream()
//                        .anyMatch(pattern -> method.toLowerCase().contains(pattern.toLowerCase())))
//                .map(methodName -> {
//                    MethodMetricsData metrics = metricService.getMethodMetrics(methodName);
//                    if (metrics != null) {
//                        log.info("Found metrics for {}: totalQueries={}, avgTime={}ms",
//                                methodName,
//                                metrics.getTotalQueries(),
//                                metrics.getAverageTime());
//                    }
//                    return metrics;
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        log.info("Returning {} order-related metrics", orderMetrics.size());
//        return orderMetrics;
//    }
    @GetMapping("/metrics/orders")
    public List<MethodMetricsData> getOrderMetrics() {
        Set<String> orderPatterns = new HashSet<>(Arrays.asList(
                "order", "delivery", "payment"
        ));
        return getMetricsForPatterns(orderPatterns);
    }
//    @GetMapping("/slow-queries/{methodName}")
//    public SlowQueriesResponse getSlowQueries(
//            @PathVariable String methodName
//    ) {
//        return SlowQueriesResponse.builder()
//                .methodName(methodName)
//                .slowQueries(metricService.getMethodSlowQueries(methodName))
//                .summary(metricService.createSlowQuerySummary(methodName))
//                .build();
//
//    }
//    @PostMapping("/thresholds/{methodName}")
//    public void setThreshold(@PathVariable String methodName,
//                             @RequestParam long slowQueryThreshold,
//                             @RequestParam long avgTimeThreshold) {
//        alertService.setThresholds(methodName, slowQueryThreshold, avgTimeThreshold);
//    }

    private List<MethodMetricsData> getMetricsForPatterns(Set<String> patterns) {
        return queryPerformanceMonitor.getQueryStats().keySet().stream()
                .filter(method -> patterns.stream()
                        .anyMatch((pattern) -> method.toLowerCase().contains(pattern)))
                .map(methodName -> {
                    MethodMetricsData metrics = metricService.getMethodMetrics(methodName);
                    if (metrics != null) {
                        log.debug("Found metrics for {}: queries{}, avgTime={}ms",
                                methodName, metrics.getTotalQueries(), metrics.getAverageTime());
                    }
                    return metrics;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
//    @GetMapping("/metrics/orders/flow")
//    public List<OrderFlowMetrics> getOrderFlowMetrics() {
//        return orderFlowMonitor.getRecentExecutions().stream()
//                .map(metrics -> OrderFlowMetrics.builder()
//                        .orderId(metrics.getOrderId())
//                        .totalTime(metrics.getTotalExecutionTime())
//                        .steps(convertStepTimings(metrics.getStepTimings()))
//                        .queryBreakdown(createQueryBreakdown(metrics.getQueryTimings()))
//                        .timestamp(metrics.getTimestamp())
//                        .build())
//                .collect(Collectors.toList());
//    }

    @GetMapping("/alerts")
    public Map<String, List<Alert>> getAlerts(){
        return  alertService.getRecentAlerts().stream()
                .collect(Collectors.groupingBy(Alert::getMethodName));
    }

    @GetMapping("/optimization-suggestions")
    public Map<String, List<String>> getOptimizationSuggestions(){
        return queryPerformanceMonitor.getQueryStats().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> analyzeAndGetSuggestions(e.getValue())
                ));
    }

    private List<String> analyzeAndGetSuggestions(QueryStats stats){
        List<String> suggestions = new ArrayList<>();

        if(stats.getAverageTime() > 500){
            suggestions.add("Consider adding relevant indexes");
        }
        if(stats.getSlowQueries().get() > 10){
            suggestions.add("High number of slow queries detected - review query patterns");
        }
        return suggestions;
    }
//    private Map<String, StepMetrics> convertStepTimings(Map<String, Long> stepTimings) {
//        long totalTime = stepTimings.values().stream().mapToLong(Long::valueOf).sum();
//
//        return stepTimings.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> StepMetrics.builder()
//                                .name(entry.getKey())
//                                .duration(entry.getValue())
//                                .queries(getQueriesForStep(entry.getKey()))
//                                .percentageOfTotal(calculatePercentage(entry.getValue(), totalTime))
//                                .build()
//                ));
//    }
//
//    private List<QueryBreakdown> createQueryBreakdown(Map<String, List<Long>> queryTimings) {
//        return queryTimings.entrySet().stream()
//                .map(entry -> {
//                    List<Long> timings = entry.getValue();
//                    return QueryBreakdown.builder()
//                            .queryType(extractQueryType(entry.getKey()))
//                            .table(extractTable(entry.getKey()))
//                            .avgDuration(calculateAverage(timings))
//                            .count(timings.size())
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }

    private List<String> getQueriesForStep(String stepName) {
        // This method would need access to query information from your monitoring system
        // You might need to modify your QueryStats or monitoring system to track queries by step
        return queryPerformanceMonitor.getQueriesForStep(stepName);
    }

    private double calculatePercentage(long stepTime, long totalTime) {
        return totalTime > 0 ? (stepTime * 100.0) / totalTime : 0;
    }

    private String extractQueryType(String queryKey) {
        // Extract query type (SELECT, INSERT, etc.) from query key
        // Example: "memberValidation.SELECT" -> "SELECT"
        return queryKey.contains(".") ? queryKey.split("\\.")[1] : queryKey;
    }

    private String extractTable(String queryKey) {
        // Extract table name from query key if available
        // This would depend on how you structure your query keys
        return queryKey.contains(".") ? queryKey.split("\\.")[0] : "";
    }

    private long calculateAverage(List<Long> timings) {
        return timings.isEmpty() ? 0 :
                timings.stream().mapToLong(Long::valueOf).sum() / timings.size();
    }



    @Data
    @Builder
    public static class OrderFlowMetrics {
        private Long orderId;
        private long totalTime;
        private Map<String, StepMetrics> steps;
        private List<QueryBreakdown> queryBreakdown;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    public static class StepMetrics {
        private String name;
        private long duration;
        private List<String> queries;
        private double percentageOfTotal;
    }

    @Data
    @Builder
    public static class QueryBreakdown {
        private String queryType;
        private String table;
        private long avgDuration;
        private long count;
    }



}
