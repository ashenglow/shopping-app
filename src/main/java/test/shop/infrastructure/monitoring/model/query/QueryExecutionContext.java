package test.shop.infrastructure.monitoring.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QueryExecutionContext {
    private final String methodKey;
    private final long executionTime;
    private final LocalDateTime timestamp;
    private final boolean isError;

    public QueryExecutionContext(String methodKey, long executionTime, boolean isError) {
        this.methodKey = methodKey;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
        this.isError = isError;
    }
}
