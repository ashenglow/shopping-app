package test.shop.domain.model.analytics;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.domain.model.item.Category;

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
    @CollectionTable(name = "category_sales")
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
}
