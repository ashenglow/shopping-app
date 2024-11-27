package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Builder;
import lombok.Data;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;

import java.util.List;

@Data
@Builder
public class OperationMetric {
    private String operationName;
    private double averageExecutionTime;
    private long executionCount;
    private List<QueryExecutionContext> recentExecutions;
}
