package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class OrderFlowMonitor {

    private final ConcurrentLinkedQueue<OrderExecutionMetrics> executionHistory = new ConcurrentLinkedQueue<>();
    private static final int MAX_HISTORY = 100;

    @Data
    @Builder
    public static class OrderExecutionMetrics {
        private Long orderId;
        private long totalExecutionTime;
        private Map<String, Long> stepTimings;
        private Map<String, List<Long>> queryTimings;
        private LocalDateTime timestamp;
    }
    @Around("execution(* test.shop.application.service.order.OrderService.order(..))")
    public Object monitorOrderFlow(ProceedingJoinPoint joinPoint) throws Throwable {
        OrderExecutionMetrics.OrderExecutionMetricsBuilder metricsBuilder = OrderExecutionMetrics.builder()
                .timestamp(LocalDateTime.now())
                .stepTimings(new HashMap<>())
                .queryTimings(new HashMap<>());

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            if (result instanceof Long) {
                metricsBuilder.orderId((Long) result);
            }
            return result;
        } finally {
            metricsBuilder.totalExecutionTime(System.currentTimeMillis() - startTime);
            executionHistory.offer(metricsBuilder.build());
            while (executionHistory.size() > MAX_HISTORY){
                executionHistory.poll();
            }
        }

    }

    public List<OrderExecutionMetrics> getRecentExecutions(){
        return new ArrayList<>(executionHistory);
    }

}
