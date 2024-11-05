package test.shop.infrastructure.monitoring.model.dashboard.stats;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MethodStats {
    private List<MethodPerformance> methodPerformance;
}
