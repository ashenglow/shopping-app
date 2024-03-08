package test.shop.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.shop.domain.item.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    public Item findItemById(Long itemId);
}
