package test.shop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import test.shop.domain.model.item.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Optional<Item> findItemById(Long itemId);
    Optional<Page<Item>> findByAndCategoryAndRatings(String category, double ratings, Pageable pageable);
}
