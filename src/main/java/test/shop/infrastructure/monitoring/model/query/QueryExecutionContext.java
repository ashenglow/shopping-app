package test.shop.infrastructure.monitoring.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class QueryExecutionContext {
    private final String methodKey;
    private final long executionTime;
    private final LocalDateTime timestamp;
    private final boolean isError;
    //for storing operation-specific data
    private Map<String, Object> metadata;

    public QueryExecutionContext(String methodKey, long executionTime, boolean isError) {
        this.methodKey = methodKey;
        this.executionTime = executionTime;
        this.timestamp = LocalDateTime.now();
        this.isError = isError;
        this.metadata = new HashMap<>();
    }


    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}
