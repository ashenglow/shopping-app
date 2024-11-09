package test.shop.infrastructure.monitoring.model.dashboard.response;

import lombok.Builder;
import lombok.Data;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.util.List;

@Data
@Builder
public class MethodMetricsData {
    private String methodName;
    private double averageTime;
    private long totalQueries;
    private long slowQueries;
    private List<SlowQueryInfo> recentSlowQueries;
    private List<TimeSeriesPoint> timeSeriesData;
}
