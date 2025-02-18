package test.shop.infrastructure.analytics.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.domain.repository.OrderRepository;

import java.util.Date;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SalesAnalyticsSchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("analytics-scheduler-");
        scheduler.setErrorHandler(throwable -> {
            log.error("Scheduled task error", throwable);
        });
        return scheduler;
    }

    }

