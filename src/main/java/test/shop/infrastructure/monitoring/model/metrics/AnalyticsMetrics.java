package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Builder;
import lombok.Data;
import test.shop.domain.model.item.Category;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AnalyticsMetrics {
    private LocalDateTime timestamp;
    private int processedOrders;
    private long processingTime;
    private Map<Category, Integer> categoryBreakdown;
    private Map<String, Long> stepTimings;
}
