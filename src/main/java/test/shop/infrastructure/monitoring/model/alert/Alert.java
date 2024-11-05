package test.shop.infrastructure.monitoring.model.alert;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Alert {
    private AlertType type;
    private String methodName;
    private AlertSeverity severity;
    private double metric;
    private double threshold;
    private LocalDateTime timestamp;
    private String details;
}
