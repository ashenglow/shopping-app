package test.shop.infrastructure.monitoring.model.dashboard.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MethodPerformance {
    private String methodName;
    private double averageTime;
    private long totalQueries;
    private long slowQueries;
}
