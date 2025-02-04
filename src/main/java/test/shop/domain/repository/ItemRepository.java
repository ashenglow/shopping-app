package test.shop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.item.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Optional<Item> findItemById(Long itemId);
    Optional<Page<Item>> findByAndCategoryAndRatings(String category, double ratings, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.stockQuantity = i.stockQuantity - :quantity " +
            "WHERE i.id = :itemId AND i.stockQuantity >= :quantity")
    int decreaseStock(@Param("itemId") Long itemId, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.stockQuantity = i.stockQuantity + :quantity " +
            "WHERE i.id = :itemId")
    int increaseStock(@Param("itemId") Long itemId, @Param("quantity") int quantity);

    @Query("SELECT i.stockQuantity FROM Item i WHERE i.id = :itemId")
    Optional<Integer> findStockQuantity(@Param("itemId") Long itemId);
}
