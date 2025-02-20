package test.shop.domain.model.analytics;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import test.shop.domain.model.item.Category;
import test.shop.domain.model.order.Order;
import test.shop.domain.model.order.OrderItem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "daily_sales_stats")
@Getter
@NoArgsConstructor
public class DailySalesStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private int totalOrders;
    private int totalRevenue;
    private double averageOrderValue;
    private int totalItems;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @CollectionTable(name = "sales_by_category",
                    joinColumns = @JoinColumn(name = "daily_sales_stats_id"))
    @Column(name = "sales")
    private Map<Category, Integer> salesByCategory = new HashMap<>();

    @Builder
    public DailySalesStats(LocalDate date, int totalOrders, int totalRevenue,
                           double averageOrderValue, int totalItems,
                           Map<Category, Integer> salesByCategory) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.totalItems = totalItems;
        this.salesByCategory = salesByCategory;
    }

    public void addOrder(Order order) {
        this.totalOrders++;
        this.totalRevenue += order.getTotalPrice();
        this.totalItems += order.getOrderItems().stream()
                .mapToInt(OrderItem::getCount)
                .sum();

        // Update category sales
        order.getOrderItems().forEach(item -> {
            if (item.getItem() != null && item.getItem().getCategory() != null) {
                Category category = item.getItem().getCategory();
                salesByCategory.merge(category, item.getTotalPrice(), Integer::sum);
            }
        });

        // Update average
        this.averageOrderValue = (double) this.totalRevenue / this.totalOrders;
    }
}
