package test.shop.infrastructure.analytics.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.shop.infrastructure.analytics.service.AnalyticsService;

@RestController
@RequestMapping("/api/public/monitoring/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @PostMapping("/run")
    public ResponseEntity<String> runAnalytics() {
        try {
            analyticsService.runAnalytics();
            return ResponseEntity.ok("Analytics completed successfully");
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Analytics failed" + e.getMessage());
        }
    }
    @Profile({"dev", "test"})
    @PostMapping("/generate/{count}")
    public ResponseEntity<String> generateSampleOrders(@PathVariable int count) {
        try {
            analyticsService.generateSampleOrders(count);
            return ResponseEntity.ok("Generated " + count + " sample orders");
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Sample generation failed: " + e.getMessage());
        }
    }

    @PostMapping("/auto/{enabled}")
    public ResponseEntity<String> setAutoAnalytics(@PathVariable boolean enabled) {
        analyticsService.setAutoAnalyticsEnabled(enabled);
        return ResponseEntity.ok("Auto analytics " + (enabled ? "enabled" : "disabled"));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetAnalytics() {
        analyticsService.resetAnalytics();
        return ResponseEntity.ok("Analytics reset completed");
    }
}
