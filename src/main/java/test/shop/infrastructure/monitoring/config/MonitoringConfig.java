package test.shop.infrastructure.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import test.shop.infrastructure.monitoring.service.AlertService;

@Configuration
@EnableAspectJAutoProxy
public class MonitoringConfig {
 @Bean
    public AlertService alertService(){
     AlertService service = new AlertService();
     // Application layer thresholds
     service.setThresholds("ItemService", 500, 200);
     service.setThresholds("OrderService", 1000, 500);

     // Domain layer thresholds
     service.setThresholds("ItemRepository", 300, 100);
     service.setThresholds("OrderRepository", 500, 200);
     service.setThresholds("SpecificationBuilderV2", 200, 100);

     // Specific method thresholds
     service.setThresholds("ItemService.findItems", 500, 200);
     service.setThresholds("ItemService.getItemDetail", 300, 100);
     service.setThresholds("OrderService.findOrdersByMemberId", 500, 200);

     return service;
 }
}
