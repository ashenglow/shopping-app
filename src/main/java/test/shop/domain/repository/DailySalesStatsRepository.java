package test.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.analytics.DailySalesStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySalesStatsRepository extends JpaRepository<DailySalesStats, Long> {
    Optional<DailySalesStats> findByDate(LocalDate date);

    @Query("SELECT ds FROM DailySalesStats ds WHERE ds.date BETWEEN :startDate AND :endDate ORDER BY ds.date")
    List<DailySalesStats> findStatsForRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("SELECT SUM(ds.totalRevenue) FROM DailySalesStats ds WHERE ds.date = :date")
    Optional<Integer> getTotalRevenueForDate(@Param("date") LocalDate date);
}
