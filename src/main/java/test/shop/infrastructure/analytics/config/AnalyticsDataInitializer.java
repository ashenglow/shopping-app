package test.shop.infrastructure.analytics.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.domain.repository.OrderRepository;


@Slf4j
@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class AnalyticsDataInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final JobLauncher jobLauncher;
    private final Job generateDailySalesReport;
    private final OrderRepository orderRepository;
    private final DailySalesStatsRepository statsRepository;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(orderRepository.count() > 0 && statsRepository.count() == 0) {
            try {
                JobParameters params = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();
                jobLauncher.run(generateDailySalesReport, params);
            }catch (Exception e) {
                log.error("Error in initial analytics", e);
            }
        }
    }
}
