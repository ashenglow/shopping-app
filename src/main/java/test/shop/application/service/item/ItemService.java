package test.shop.application.service.item;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.model.item.Image;
import test.shop.domain.model.item.Item;
import test.shop.utils.Range;
import test.shop.application.dto.response.ProductDetailDto;
import test.shop.application.dto.request.ProductDto;
import test.shop.domain.repository.ItemRepository;
import test.shop.infrastructure.persistence.jpa.repository.SpecificationBuilderV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(ProductDto dto) {
        Item item = buildItemFromDto(dto);
        return itemRepository.save(item).getId();
    }

    @Transactional
    public void updateItem(Long itemId, ProductDto dto) {
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        item.updateFromDto(dto);
    }

    @Transactional
    public void addItemImage(Long itemId, String imageUrl){
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("item not found"));
        item.addImage(imageUrl);
    }

    @Transactional
    public void reorderItemImage(Long itemId, Long imageId, int newPosition){
       Item item = itemRepository.findItemById(itemId)
               .orElseThrow(() -> new IllegalArgumentException("item not found"));
       item.reorderImage(imageId, newPosition);
    }

    public Page<ProductDto> findItems(int page, int size, Range<Integer> range, String category, int ratings) {
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("ratings", ratings);
        params.put("price", range);
        Pageable pageable = PageRequest.of(page, size);
        Specification<Item> spec = new SpecificationBuilderV2<Item>().buildSpecification(params);

        return itemRepository.findAll(spec, pageable)
                .map(Item::toProductDto);

    }

    public long getItemCount() {
        return itemRepository.count();
    }

    @Transactional
    public void removeItemImage(Long itemId, Long imageId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.removeImage(imageId);
    }

    public List<ProductDto> findAll() {
        List<ProductDto> dtos = itemRepository.findAll().stream().map(Item::toProductDto).collect(Collectors.toList());
        return dtos;
    }

    public Item findOne(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
    }

    public ProductDetailDto getItemDetail(Long itemId) {
        return itemRepository.findItemById(itemId)
                .map(Item::toProductDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    }



    @Transactional
    public void deleteItem(Long itemId) {
        if(!itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Item not found");
        }
        itemRepository.deleteById(itemId);
    }

    private Item buildItemFromDto(ProductDto dto){
        Item item = Item.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .description(dto.getDescription())
                .ratings(0)
                .numOfReviews(0)
                .category(dto.getCategory())
                .build();
        if(dto.getImages() != null){
            dto.getImages().forEach(imgDto -> item.addImage(imgDto.getUrl()));
        }
        return item;
    }


}
