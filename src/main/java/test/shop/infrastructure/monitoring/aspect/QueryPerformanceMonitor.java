package test.shop.infrastructure.monitoring.aspect;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.metrics.QueryMetrics;
import test.shop.infrastructure.monitoring.model.metrics.QueryStats;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;
import test.shop.infrastructure.monitoring.service.AlertService;

import javax.swing.plaf.metal.MetalBorders;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.hibernate.grammars.hql.HqlParser.*;


@Aspect
@Component
@Slf4j
public class QueryPerformanceMonitor {

    private final Map<String, QueryStats> queryStats = new ConcurrentHashMap<>();
    private final Queue<QueryExecutionContext> queryHistory = new ConcurrentLinkedQueue<>();
    private final AlertService alertService;
    private static final int MAX_HISTORY = 1000;

    public QueryPerformanceMonitor(AlertService alertService) {
        this.alertService = alertService;
    }

    // Split into multiple pointcuts for better control
    @Pointcut("within(test.shop.application.service..*)")
    public void applicationService() {}

    @Pointcut("within(test.shop.application.service.item.*)")
    public void itemService() {}

    @Pointcut("within(test.shop.application.service.order.*)")
    public void orderService() {}

    @Pointcut("within(test.shop.domain.repository.*)")
    public void domainRepository() {}

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethod() {}
    @Around("applicationService() || itemService() || orderService() || domainRepository() || transactionalMethod()")
    public Object monitorQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodKey = extractMethodKey(joinPoint);
        Object[] args = joinPoint.getArgs();

        // Log method entry for debugging
        log.debug("Entering method: {} with args: {}", methodKey, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        boolean isError = false;

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            // Log successful execution
            log.debug("Completed method: {} in {}ms", methodKey, duration);

            return result;
        } catch (Exception e){
            isError = true;
            log.error("Error in monitored method: {} - {}", methodKey, e.getMessage());
            throw e;
        }finally {
            long executionTime = System.currentTimeMillis() - startTime;
            processQueryExecution(methodKey, executionTime, isError);
        }
    }

    // Improved method key generation for better identification
    private String getMethodSignature(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }

    private String extractMethodKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String className = targetClass.getSimpleName();

        // Handle proxy classes
        if (className.contains("$")) {
            Class<?>[] interfaces = targetClass.getInterfaces();
            if (interfaces.length > 0 && !interfaces[0].getSimpleName().contains("$")) {
                className = interfaces[0].getSimpleName();
            } else if (targetClass.getSuperclass() != null) {
                className = targetClass.getSuperclass().getSimpleName();
            } else {
                className = className.substring(0, className.indexOf('$'));
            }
        }

        return className + "." + signature.getName();
    }

    private void processQueryExecution(String methodKey, long executionTime, boolean isError) {
        QueryStats stats = queryStats.computeIfAbsent(
                methodKey,
                k -> new QueryStats(methodKey)
        );

        // Use your existing AlertThreshold constructor
        AlertThreshold threshold = getThresholdForMethod(methodKey);

        stats.addQuery(executionTime, threshold);

        if (executionTime > threshold.getSlowQueryThreshold()) {
            log.warn("Slow execution detected - Method: {}, Time: {}ms",
                    methodKey, executionTime);
        }

        QueryExecutionContext context = new QueryExecutionContext(
                methodKey,
                executionTime,
                isError
        );

        queryHistory.offer(context);
        while (queryHistory.size() > MAX_HISTORY) {
            queryHistory.poll();
        }

        alertService.checkThresholds(methodKey, executionTime, stats);
    }

    private AlertThreshold getThresholdForMethod(String methodKey) {
        // Default thresholds
        long slowQueryThreshold = 1000;
        long avgTimeThreshold = 500;

        // Specific thresholds for different types of methods
        if (methodKey.startsWith("ItemService")) {
            slowQueryThreshold = 500;
            avgTimeThreshold = 200;
        } else if (methodKey.startsWith("OrderService")) {
            slowQueryThreshold = 1000;
            avgTimeThreshold = 500;
        } else if (methodKey.endsWith("Repository")) {
            slowQueryThreshold = 300;
            avgTimeThreshold = 100;
        }

        return new AlertThreshold(slowQueryThreshold, avgTimeThreshold);
    }

    @PostConstruct
    public void initializeMonitoring() {
        // Service methods
        alertService.setThresholds("ItemService.findItems", 500, 200);
        alertService.setThresholds("ItemService.getItemDetail", 300, 100);
        alertService.setThresholds("ItemService.saveItem", 400, 200);

        alertService.setThresholds("OrderService.order", 1000, 500);
        alertService.setThresholds("OrderService.findOrdersByMemberId", 500, 200);

        // Repository methods
        alertService.setThresholds("ItemRepository.findAll", 300, 100);
        alertService.setThresholds("OrderRepository.findOrdersByMemberId", 500, 200);

        log.info("Performance monitoring initialized with {} thresholds",
                queryStats.size());
    }

    public Map<String, QueryStats> getQueryStats(){
        return new HashMap<>(queryStats);
    }

}
