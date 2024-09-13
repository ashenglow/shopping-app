package test.shop.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import test.shop.domain.item.Category;
import test.shop.web.dto.ProductDto;

import java.util.Optional;

public interface CustomItemRepository {
    Page<ProductDto> findItemsWithFilters(Pageable pageable, Integer minPrice, Integer maxPrice, Category category, Integer minRating);


}
