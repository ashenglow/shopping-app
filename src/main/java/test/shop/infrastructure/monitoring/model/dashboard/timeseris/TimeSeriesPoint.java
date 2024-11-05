package test.shop.infrastructure.monitoring.model.dashboard.timeseris;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class TimeSeriesPoint {
    private LocalDateTime timestamp;
    private double value;
    private String methodName;

    public TimeSeriesPoint(LocalDateTime timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public TimeSeriesPoint(LocalDateTime timestamp, double value, String methodName) {
        this.timestamp = timestamp;
        this.value = value;
        this.methodName = methodName;
    }
}
