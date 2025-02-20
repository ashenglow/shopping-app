package test.shop.infrastructure.analytics.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import test.shop.domain.model.analytics.DailySalesStats;
import test.shop.domain.model.item.Category;
import test.shop.domain.model.order.Order;
import test.shop.domain.model.order.OrderItem;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SalesAnalyticsBatchConfig {
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final QueryPerformanceMonitor monitor;
    private final PlatformTransactionManager transactionManager;
    private final DailySalesStatsRepository statsRepository;

    @Bean
    public Job generateDailySalesReport(JobRepository jobRepository){
        return new JobBuilder("dailySalesReportJob", jobRepository)
                .start(aggregateDailySales())
                .build();

    }

    @Bean
    public Step aggregateDailySales(){
        return new StepBuilder("aggregateDailySales", jobRepository)
                .<Order, DailySalesStats>chunk(100, transactionManager)
                .reader(orderReader())
                .processor(statsProcessor())
                .writer(statsWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Order> orderReader() {
        // include today's orders for test
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startOfDay = today.with(LocalTime.MIN);
        LocalDateTime endOfDay = today.with(LocalTime.MAX);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startOfDay);
        parameters.put("endDate", endOfDay);

        return new JpaPagingItemReaderBuilder<Order>()
                .name("orderReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        "SELECT o FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate"
                )
                .parameterValues(parameters)
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Order, DailySalesStats> statsProcessor(){
        return new ItemProcessor<Order, DailySalesStats>() {
            private final Map<LocalDate, DailySalesStats> accumulator = new HashMap<>();

            @Override
            public DailySalesStats process(Order order) throws Exception {
                try {
                    LocalDate orderDate = order.getLocalDate();

                    if (orderDate == null) {
                        log.warn("Skipping order {} with null date", order.getId());
                        return null;
                    }

                    // Get or create stats for this date
                    DailySalesStats stats = accumulator.computeIfAbsent(orderDate, date ->
                            DailySalesStats.builder()
                                    .date(date)
                                    .totalOrders(0)
                                    .totalRevenue(0)
                                    .averageOrderValue(0)
                                    .totalItems(0)
                                    .salesByCategory(new HashMap<>())
                                    .build()
                    );

                    // Update stats
                    stats.addOrder(order);

                    // Only return stats when all orders for this date are processed
                    return stats;

                } catch (Exception e) {
                    log.error("Error processing order {}: {}", order.getId(), e.getMessage());
                    return null;
                }
            }
        };
    }
    @Bean
    public JpaItemWriter<DailySalesStats> statsWriter(){
        JpaItemWriter<DailySalesStats> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);

        try {
            // delete existing stats for the date range being processed
            writer.afterPropertiesSet();
        }catch (Exception e){
            throw new RuntimeException("Failed to initialize JpaItemWriter",e);
        }

        return writer;
    }
}
