package test.shop.infrastructure.analytics.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.domain.model.analytics.DailySalesStats;
import test.shop.domain.model.item.Category;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.infrastructure.analytics.service.AnalyticsService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/public/monitoring/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final DailySalesStatsRepository statsRepository;
    private final Environment environment;

    @PostMapping("/run")
    public ResponseEntity<String> runAnalytics() {
        try {
            // reset before running in dev environment
            if(Arrays.asList("dev", "test").contains(environment.getActiveProfiles())){
                analyticsService.resetAnalytics();
            }
            analyticsService.runAnalytics();
            return ResponseEntity.ok("Analytics completed successfully");
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Analytics failed" + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAnalyticsStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        Map<String, Object> stats = new HashMap<>();

        // get today's stats
        Optional<DailySalesStats> todayStatsOpt = statsRepository.findByDate(today);
        if (todayStatsOpt.isPresent()) {
            DailySalesStats todayStats = todayStatsOpt.get();
            Map<String, Object> todayData = new HashMap<>();
            todayData.put("totalOrders", todayStats.getTotalOrders());
            todayData.put("totalRevenue", todayStats.getTotalRevenue());
            todayData.put("averageOrderValue", todayStats.getAverageOrderValue());
            todayData.put("categoryBreakdown", todayStats.getSalesByCategory());
            stats.put("today", todayData);
            log.info("Found today's stats: {}", todayData);
        }else {
            log.warn("No stats found for today ({})", today);
        }



        // get weekly stats
        List<DailySalesStats> weeklyStats = statsRepository.findStatsForRange(weekAgo, today);
        Map<String, Integer> weeklyData = weeklyStats.stream()
                .collect(Collectors.toMap(
                        dailyStats -> dailyStats.getDate().toString(),
                        DailySalesStats::getTotalRevenue
                ));
        stats.put("weekly", weeklyData);

        // add category totals
        Map<Category, Integer> categoryTotals = weeklyStats.stream()
                .flatMap(stat -> stat.getSalesByCategory().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue)
                ));
        stats.put("categoryTotals", categoryTotals);

        return ResponseEntity.ok(stats);
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

    @GetMapping("/range")
    public ResponseEntity<List<DailySalesStats>> getStatsForRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(statsRepository.findStatsForRange(start, end));
    }

}
