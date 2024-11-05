package test.shop.infrastructure.monitoring.model.dashboard.response;

import lombok.Builder;
import lombok.Data;
import test.shop.infrastructure.monitoring.model.dashboard.summary.SlowQuerySummary;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.util.List;

@Data
@Builder
public class SlowQueriesResponse {
    private String methodName;
    private List<SlowQueryInfo> slowQueries;
    private SlowQuerySummary summary;
}
