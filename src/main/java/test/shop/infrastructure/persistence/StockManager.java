package test.shop.infrastructure.persistence;

public interface StockManager<T> {
    void decreaseStock(Long id, int quantity);
    void increaseStock(Long id, int quantity);
    int getCurrentStock(Long id);
}
