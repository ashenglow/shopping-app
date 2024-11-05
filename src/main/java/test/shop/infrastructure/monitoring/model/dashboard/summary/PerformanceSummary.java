package test.shop.infrastructure.monitoring.model.dashboard.summary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerformanceSummary {
    private int totalMethods;
    private long totalQueries;
    private long totalSlowQueries;
    private double averageResponseTime;
}
