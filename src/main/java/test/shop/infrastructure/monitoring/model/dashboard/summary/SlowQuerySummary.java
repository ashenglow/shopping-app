package test.shop.infrastructure.monitoring.model.dashboard.summary;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SlowQuerySummary {
    private int totalSlowQueries;
    private double averageSlowQueryTime;
    private long maxSlowQueryTime;
    private LocalDateTime mostRecentSlowQuery;
}
