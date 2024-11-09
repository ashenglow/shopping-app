package test.shop.infrastructure.monitoring.model.alert;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertThreshold {
    private long slowQueryThreshold;
    private long avgTimeThreshold;
    private int consecutiveSlowQueries;

    public AlertThreshold(long slowQueryThreshold, long avgTimeThreshold) {
        this.slowQueryThreshold = slowQueryThreshold;
        this.avgTimeThreshold = avgTimeThreshold;
        this.consecutiveSlowQueries = 3;
    }
}
