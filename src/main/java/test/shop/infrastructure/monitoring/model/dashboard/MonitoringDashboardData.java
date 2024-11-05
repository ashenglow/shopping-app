package test.shop.infrastructure.monitoring.model.dashboard;

import lombok.Builder;
import lombok.Data;
import test.shop.infrastructure.monitoring.model.dashboard.stats.MethodStats;
import test.shop.infrastructure.monitoring.model.dashboard.summary.PerformanceSummary;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;

import java.util.List;

@Data
@Builder
public class MonitoringDashboardData {
private PerformanceSummary summary;
private List<TimeSeriesPoint> timeSeriesData;
private MethodStats methodStats;
}
