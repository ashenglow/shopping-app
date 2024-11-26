package test.shop.infrastructure.monitoring.aspect;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.actuate.endpoint.OperationType;
import org.springframework.stereotype.Component;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;
import test.shop.infrastructure.monitoring.service.AlertService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AutomatedPerformanceMonitor {
    private final Map<String, QueryStats> queryStats = new ConcurrentHashMap<>();
    private final AlertService alertService;
    private static final int MAX_HISTORY = 1000;
    private final Queue<QueryExecutionContext> queryHistory = new ConcurrentLinkedQueue<>();

    @Pointcut("within(@org.springframework.stereotype.Repository *) || " +
            "within(@org.springframework.stereotype.Service *)")
    public void repositoryOrServiceMethod(){}

    @Around("repositoryOrServiceMethod()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodKey = extractMethodKey(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        boolean isError = false;
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            isError = true;
            throw e;
        }finally {
            long executionTime = System.currentTimeMillis() - startTime;
            QueryExecutionContext context = new QueryExecutionContext(
                    methodKey,
                    executionTime,
                    isError
            );

            // Add operation type metadata
            OperationType operationType = detectOperationType(methodKey, targetClass);
            context.addMetadata("operationType", operationType.name());
            context.addMetadata("targetClass", targetClass.getSimpleName());

            // Add parameter metadata
            captureRelevantParameters(context, joinPoint);

            // Record execution
            recordExecution(context);

            //analyze if it's a slow operation
            if(operationType == OperationType.OTHER && executionTime > 500){
                QueryAnalysis analysis = analyzeQueryPattern(context);
                log.warn("Slow order operation detected: {} took {}ms\nAnalysis: {}",
                        methodKey, executionTime, analysis);
            }

        }


    }

    private void recordExecution(QueryExecutionContext context) {
        // get or create QueryStats for this method
        QueryStats stats = queryStats.computeIfAbsent(
                context.getMethodKey(),
                k -> new QueryStats(context.getMethodKey())
        );
        // record the execution in stats
        stats.addQuery(context);

        // add to history with size limit
        queryHistory.offer(context);
        while (queryHistory.size() > MAX_HISTORY) {
            queryHistory.poll();
        }

        // check for alerts if execution time exceeds thresholds
        AlertThreshold threshold = getThresholdForMethod(context.getMethodKey());
        if(context.getExecutionTime() > threshold.getSlowQueryThreshold()){
            alertService.handleSlowQuery(context, threshold);
        }
        log.debug("Recorded execution for {}: {}ms", context.getMethodKey(), context.getExecutionTime());

    }

    private AlertThreshold getThresholdForMethod(String methodKey) {
        // default thresholds
        long slowQueryThreshold = 1000;
        long avgTimeThreshold = 500;

        // method-specific thresholds
        if(methodKey.startsWith("OrderService")){
            slowQueryThreshold = 2000;
            avgTimeThreshold = 1000;
        } else if (methodKey.startsWith("ItemService")){
            slowQueryThreshold = 500;
            avgTimeThreshold =200;
        }
        return new AlertThreshold(slowQueryThreshold, avgTimeThreshold);
    }

    private String extractMethodKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String className = targetClass.getSimpleName();

        if (className.contains("$")) {
            Class<?>[] interfaces = targetClass.getInterfaces();
            if (interfaces.length > 0 && !interfaces[0].getSimpleName().contains("$")) {
                className = interfaces[0].getSimpleName();
            }else if(targetClass.getSuperclass() != null){
                className = targetClass.getSuperclass().getSimpleName();
            }else{
                className = className.substring(0, className.indexOf("$"));
            }
        }

        return className + "." + signature.getName();
    }

    @Data
    @Builder
    public static class QueryAnalysis{
        private final String queryPattern;
        private final long executionTime;
        @Builder.Default
        private final List<String> optimizationSuggestions = new ArrayList<>();
        @Builder.Default
        private final Map<String, Object> metrics = new HashMap<>();

        public String generateReport(){
            StringBuilder report = new StringBuilder();
            report.append("Query Analysis Report\n");
            report.append("====================\n");
            report.append(String.format("Pattern: %s\n", queryPattern));
            report.append(String.format("Execution Time: %dms\n\n", executionTime));

            report.append("Optimization Suggestions: %s\n");
            for (String suggestion : optimizationSuggestions) {
                report.append("- ").append(suggestion).append("\n");

            }

            report.append("\nMetrics:\n");
            for (Map.Entry<String, Object> entry : metrics.entrySet()) {
                report.append(String.format("- %s: %s\n", entry.getKey(), entry.getValue()));
            }
            return report.toString();
        }

    }

    private QueryAnalysis analyzeQueryPattern(QueryExecutionContext context){
        QueryAnalysis analysis = QueryAnalysis.builder()
                .queryPattern(detectQueryPattern(context))
                .executionTime(context.getExecutionTime())
                .build();

        //analyze based on operation type
        String operationType = (String) context.getMetadata().get("operationType");
        if("ORDER".equals(operationType)){
            analyzeOrderQuery(context, analysis);
        }else if ("ITEM".equals(operationType)){
            analyzeItemQuery(context, analysis);
        }

        return analysis;
    }
    private String detectQueryPattern(QueryExecutionContext context){
        Map<String, Object> metadata = context.getMetadata();
        if(metadata.containsKey("param.orderId") && context.getExecutionTime() > 500){
            return "POTENTIAL_N_PLUS_1";
        }else if(metadata.containsKey("param.memberId") && context.getExecutionTime() > 300){
            return "POSSIBLE_MISSING_INDEX";
        }
        return "NORMAL";
    }

    private void analyzeOrderQuery(QueryExecutionContext context, QueryAnalysis analysis){

        //add suggestions to the built object
        if(context.getExecutionTime() > 1000){
            analysis.getOptimizationSuggestions().add(
                    "Consider adding composite index (order_id, item_id) for faster lookups"
            );
        }
        if(context.getMethodKey().contains("findOrderById")){
            analysis.getOptimizationSuggestions().add(
                    "Potential N+1 query detected. Consider using join fetch:\n" +
                            "SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id"
            );
        }

        // add metrics
        analysis.getMetrics().put("avgOrderTime",
                calculateAverageProcessingTime(context.getMethodKey())
                );
        log.info("Analysis complete: {}", analysis.getOptimizationSuggestions());
    }

    private void analyzeItemQuery(QueryExecutionContext context, QueryAnalysis analysis){

        if(context.getMethodKey().contains("findBy")){
            analysis.getOptimizationSuggestions().add(
                    "Verify index usage on frequently searched columns"
            );
        }
        analysis.getMetrics().put("avgQueryTime",
                calculateAverageProcessingTime(context.getMethodKey()));
    }

    private double calculateAverageProcessingTime(String methodKey){
        QueryStats stats = queryStats.get(methodKey);
        return stats != null ? stats.getAverageTime() : 0.0;
    }
    public Map<String, QueryStats> getQueryStats() {
        return new HashMap<>(queryStats);
    }

    public List<QueryExecutionContext> getRecentExecutions(){
        return new ArrayList<>(queryHistory);
    }

    private enum OperationType {
        ORDER, ITEM, MEMBER, OTHER
    }

    private OperationType detectOperationType(String methodKey, Class<?> targetClass) {
        String key = methodKey.toLowerCase();
        if(key.contains("order")) return OperationType.ORDER;
        if(key.contains("item")) return OperationType.ITEM;
        if(key.contains("member")) return OperationType.MEMBER;
        return OperationType.OTHER;
    }

    private void captureRelevantParameters(QueryExecutionContext context, ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        for(int i =0; i < args.length; i++) {
            if(args[i] != null){
                String paramName = paramNames[i];
                if(paramName.toLowerCase().contains("id") ||
                args[i] instanceof Long ||
                args[i] instanceof Integer) {
                    context.addMetadata("param." + paramName, args[i].toString());
                }
            }
        }
    }

}
