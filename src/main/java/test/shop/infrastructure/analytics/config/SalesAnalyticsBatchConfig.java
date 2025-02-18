package test.shop.infrastructure.analytics.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
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
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class SalesAnalyticsBatchConfig {
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final QueryPerformanceMonitor monitor;
    private final PlatformTransactionManager transactionManager;

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
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        Properties jpaProperties = new Properties();
        jpaProperties.put("jakarta.persistence.query.timeout", 600000);

        return new JpaPagingItemReaderBuilder<Order>()
                .name("orderReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        "SELECT o FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate"
                )
                .parameterValues(Map.of(
                        "startDate", yesterday.with(LocalTime.MIN),
                        "endDate", yesterday.with(LocalTime.MAX)
                ))
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<test.shop.domain.model.order.Order, DailySalesStats> statsProcessor(){
        return new ItemProcessor<Order, DailySalesStats>() {
            private final Map<Category, Integer> categorySales = new HashMap<>();
            private int totalOrders = 0;
            private int totalRevenue = 0;
            private int totalItems = 0;

            @Override
            public DailySalesStats process(test.shop.domain.model.order.Order order) throws Exception {
                totalOrders++;
                totalRevenue += order.getTotalPrice();

                for (OrderItem item : order.getOrderItems()) {
                    Category category = item.getItem().getCategory();
                    categorySales.merge(category, item.getTotalPrice(), Integer::sum);
                    totalItems += item.getCount();
                }

                return DailySalesStats.builder()
                        .date(order.getLocalDate())
                        .totalOrders(totalOrders)
                        .totalRevenue(totalRevenue)
                        .averageOrderValue(totalRevenue / totalOrders)
                        .totalItems(totalItems)
                        .salesByCategory(new HashMap<>(categorySales))
                        .build();

            }
        };
    }
    @Bean
    public JpaItemWriter<DailySalesStats> statsWriter() {
        JpaItemWriter<DailySalesStats> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
