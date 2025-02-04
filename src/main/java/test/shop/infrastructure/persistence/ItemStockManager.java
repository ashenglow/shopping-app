package test.shop.infrastructure.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.exception.NotEnoughStockException;
import test.shop.domain.model.item.Item;
import test.shop.domain.repository.ItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemStockManager implements StockManager<Item> {
    private final ItemRepository itemRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseStock(Long itemId, int quantity) {
        validateQuantity(quantity);
        int updated = itemRepository.decreaseStock(itemId, quantity);
        if(updated == 0){
            Integer currentStock = itemRepository.findStockQuantity(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다"));
            if(currentStock < quantity){
                throw new NotEnoughStockException(
                        String.format("재고 부족. 요청: %d, 현재 재고: %d", quantity, currentStock)
                );
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void increaseStock(Long itemId, int quantity) {
        validateQuantity(quantity);
        int updated = itemRepository.increaseStock(itemId, quantity);
        if (updated == 0){
            throw new EntityNotFoundException("상품을 찾을 수 없습니다");
        }
    }

    @Override
    public int getCurrentStock(Long itemId) {
        return itemRepository.findStockQuantity(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다"));
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다");
        }
    }
}
