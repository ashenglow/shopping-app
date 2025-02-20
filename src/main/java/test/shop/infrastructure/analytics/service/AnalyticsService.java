package test.shop.infrastructure.analytics.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.model.delivery.Delivery;
import test.shop.domain.model.delivery.DeliveryStatus;
import test.shop.domain.model.item.Item;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.order.Order;
import test.shop.domain.model.order.OrderItem;
import test.shop.domain.repository.DailySalesStatsRepository;
import test.shop.domain.repository.ItemRepository;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final DailySalesStatsRepository statsRepository;
    private final JobLauncher jobLauncher;
    private final Job generateDailySalesReport;
    private final EntityManager entityManager;

    private final AtomicBoolean autoAnalyticsEnabled = new AtomicBoolean(false);

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if(orderRepository.count() > 0 && statsRepository.count() == 0){
            runAnalytics();
        }
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void scheduledAnalytics() {
        if(autoAnalyticsEnabled.get()){
            runAnalytics();
        }
    }

    public void runAnalytics() {
        try {
            // create unique job parameters each time
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("source", UUID.randomUUID().toString())
                    .toJobParameters();

            jobLauncher.run(generateDailySalesReport, params);
            log.info("Analytics batch job completed successfully");
        } catch (Exception e) {
            log.error("Error running Analytics batch job", e);
            throw new RuntimeException("Analytics job failed", e);
        }
    }
    @Transactional
    public void generateSampleOrders(int count){
        List<Member> members = memberRepository.findAll();
        List<Item> items = itemRepository.findAll();

        if(members.isEmpty() || items.isEmpty()){
            throw new IllegalStateException("No members or items found for sample orders");
        }
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            Member randomMember = members.get(random.nextInt(members.size()));
            List<OrderItem> orderItems = createRandomOrderItems(items, random);

            // create delivery
            Delivery delivery = new Delivery();
            delivery.saveAddress(randomMember.getAddress());
            delivery.saveStatus(DeliveryStatus.READY);

            // create order
            Order order = Order.createOrder(randomMember, delivery, orderItems);

            // set random date within last 7 days
            LocalDateTime orderDate = now.minusDays(random.nextInt(7))
                    .withHour(random.nextInt(24))
                    .withMinute(random.nextInt(60));

            orderRepository.save(order);
        }

        log.info("Generated {} sample orders", count);
    }

    private List<OrderItem> createRandomOrderItems(List<Item> items, Random random) {
        List<OrderItem> orderItems = new ArrayList<>();
        int itemCount = random.nextInt(3) + 1; // 1-3 items per order

        for (int i = 0; i < itemCount; i++) {
            Item randomItem = items.get(random.nextInt(items.size()));
            OrderItem orderItem = OrderItem.createRandomOrderItem(
                    randomItem,
                    random.nextInt(3) + 1 // 1-3 quantity
            );
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public void setAutoAnalyticsEnabled(boolean enabled){
        autoAnalyticsEnabled.set(enabled);
        log.info("Auto analytics {}.", enabled ? "enabled" : "disabled");
    }
    public boolean isAutoAnalyticsEnabled() {
        return autoAnalyticsEnabled.get();
    }
    @Transactional
    public void resetAnalytics() {
        try {
            // delete from the collection table
            entityManager.createNativeQuery("DELETE FROM sales_by_category").executeUpdate();
            //delete from the main table
            entityManager.createNativeQuery("DELETE FROM daily_sales_stats").executeUpdate();

            log.info("Analytics data reset completed");
        }catch (Exception e){
            log.error("Failed to reset analytics data", e);
            throw new RuntimeException("Failed to reset analytics data", e);
        }
    }
}
