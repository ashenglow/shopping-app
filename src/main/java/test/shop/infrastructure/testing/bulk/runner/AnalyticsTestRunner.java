package test.shop.infrastructure.testing.bulk.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;
import test.shop.infrastructure.monitoring.model.metrics.OrderFlowMonitor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsTestRunner implements CommandLineRunner {
    private final JobLauncher jobLauncher;
    private final Job generateDailySalesReport;
    private final OrderFlowMonitor orderFlowMonitor;

    @Override
    public void run(String... args) throws Exception {
        if(Arrays.asList(args).contains("--run-analytics")){
            runAnalyticsJob();
        }
    }

    public void runAnalyticsJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .addString("source", "test")
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(generateDailySalesReport, params);

            // record metrics
            if(execution.getStatus() == BatchStatus.COMPLETED){
                recordAnalyticsMetrics(execution);
            }
        }catch (Exception e){
            log.error("Failed to run analytics job", e);
        }
    }

    private void recordAnalyticsMetrics(JobExecution execution){
        OrderFlowMonitor.OrderExecutionMetrics metrics = OrderFlowMonitor.OrderExecutionMetrics.builder()
                .totalExecutionTime(
                        ChronoUnit.MILLIS.between(
                                execution.getStartTime(),
                                execution.getEndTime()
                        )
                )
                .stepTimings(Map.of(
                        "analyticsProcessing",
                        ChronoUnit.MILLIS.between(
                                execution.getStartTime(),
                                execution.getEndTime()
                        )
                ))
                .timestamp(LocalDateTime.now())
                .build();

        orderFlowMonitor.getRecentExecutions().add(metrics);

    }
}
