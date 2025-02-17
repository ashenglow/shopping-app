package test.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.analytics.DailySalesStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySalesStatsRepository extends JpaRepository<DailySalesStats, Long> {
    Optional<DailySalesStats> findByDate(LocalDate date);
    List<DailySalesStats> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
