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
import test.shop.application.dto.response.OrderDto;
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
    private final Map<String, List<String>> stepQueries = new ConcurrentHashMap<>();
    private final ThreadLocal<String> currentStep = new ThreadLocal<>();
    private final AlertService alertService;
    private static final int MAX_HISTORY = 1000;

    public QueryPerformanceMonitor(AlertService alertService) {
        this.alertService = alertService;
    }

    @Around("execution(* test.shop.application.service.order.OrderService.*(..)) || " +
            "execution(* test.shop.domain.repository.OrderRepository.*(..)) || " +
            "execution(* test.shop.application.service.item.ItemService.*(..)) || " +
            "execution(* test.shop.domain.repository.ItemRepository.*(..)) || " +
            "execution(* test.shop.application.service.member.MemberService.*(..)) || " +
            "execution(* test.shop.domain.repository.MemberRepository.*(..)) || " +
            "execution(* test.shop.domain.model.delivery.Delivery.*(..)) || " +
            "execution(* test.shop.domain.model.order.Order.createOrder(..)) || " +
            "execution(* test.shop.domain.model.order.OrderItem.createOrderItem(..)) || " +
            "execution(* test.shop.domain.model.order.Order.addOrderItem(..))")
    public Object monitorQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodKey = extractMethodKey(joinPoint);
        long startTime = System.currentTimeMillis();
        boolean isError = false;

        try {
            Object result = joinPoint.proceed();

            // Only record step query if we're in a step
            String step = currentStep.get();
            if (step != null) {
                stepQueries.computeIfAbsent(step, k -> new ArrayList<>())
                        .add(methodKey);
            }

            return result;
        } catch (Exception e) {
            isError = true;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            QueryExecutionContext context = new QueryExecutionContext(methodKey, executionTime, isError);

            QueryStats stats = queryStats.computeIfAbsent(methodKey, k -> new QueryStats(methodKey));
            stats.addQuery(context);

            log.info("Recorded method execution: {} took {}ms", methodKey, executionTime);
        }
    }

    // Step tracking methods
    public void setCurrentStep(String step) {
        if (step != null) {
            currentStep.set(step);
            log.debug("Set current step to: {}", step);
        }
    }

    public String getCurrentStep() {
        return currentStep.get();
    }

    public void clearCurrentStep() {
        currentStep.remove();
    }

    // Query tracking methods
    public List<String> getQueriesForStep(String stepName) {
        return stepName != null ?
                stepQueries.getOrDefault(stepName, new ArrayList<>()) :
                new ArrayList<>();
    }

    public Map<String, List<String>> getAllStepQueries() {
        return new HashMap<>(stepQueries);
    }

    private String extractMethodKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String className = targetClass.getSimpleName();

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

    public Map<String, QueryStats> getQueryStats() {
        return new HashMap<>(queryStats);
    }

    @PostConstruct
    public void initializeMonitoring() {
        // Set default thresholds
        alertService.setThresholds("ItemService.findItems", 500, 200);
        alertService.setThresholds("ItemService.getItemDetail", 300, 100);
        alertService.setThresholds("OrderService.order", 1000, 500);
        alertService.setThresholds("OrderRepository.findOrdersByMemberId", 500, 200);

        log.info("Performance monitoring initialized");
    }
}
