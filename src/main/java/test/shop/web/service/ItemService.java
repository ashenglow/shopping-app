package test.shop.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.item.Item;
import test.shop.utils.Range;
import test.shop.web.dto.ProductDetailDto;
import test.shop.web.dto.ProductDto;
import test.shop.web.repository.ItemRepository;
import test.shop.web.repository.SpecificationBuilder;
import test.shop.web.repository.SpecificationBuilderV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(ProductDto form) {
        Item item = new Item();
        item.saveItem(form);
        itemRepository.save(item);

    }

    public Page<ProductDto> findItems(int page, int size, Range<Integer> range, String category, int ratings) {
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("ratings", ratings);
        params.put("price", range);
        Pageable pageable = PageRequest.of(page, size);
        Specification<Item> spec = new SpecificationBuilderV2<Item>().buildSpecification(params);
        if (spec == null) {
            return itemRepository.findAll(pageable).map(Item::newProductDto);
        }
        return itemRepository.findAll(spec, pageable).map(Item::newProductDto);

    }

    public List<ProductDto> findAll() {
        List<ProductDto> dtos = itemRepository.findAll().stream().map(Item::newProductDto).collect(Collectors.toList());
        return dtos;
    }

    public Item findOne(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
    }

    public ProductDetailDto getItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
        return item.newProductDetailDto(item);

    }


    @Transactional
    public void updateItem(Item item, Long itemId) {
        Item foundItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
        foundItem.updateItem(item.getName(), item.getPrice(), item.getStockQuantity(), item.getDescription(), item.getRatings(), item.getNumOfReviews(), item.getCategory());
        itemRepository.save(item);

    }

    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
