package test.shop.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.item.Item;
import test.shop.domain.model.review.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<List<Review>> findByItemId(Long itemId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.item = :item")
    long countByItem(@Param("item") Item item);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item = :item")
    double getAverageRatingByItem(@Param("item") Item item);

}
