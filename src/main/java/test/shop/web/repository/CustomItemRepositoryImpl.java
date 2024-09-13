package test.shop.web.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import test.shop.domain.item.Category;
import test.shop.web.dto.ImageDto;
import test.shop.web.dto.ProductDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomItemRepositoryImpl implements CustomItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ProductDto> findItemsWithFilters(Pageable pageable,  Integer minPrice, Integer maxPrice, Category category,Integer minRating) {
        String jpql = "SELECT NEW test.shop.web.dto.ProductDto(i.id, i.name, i.price, " +
                "i.stockQuantity, i.ratings, i.numOfReviews, i.category) " +
                "FROM Item i " +
                "WHERE (:category IS NULL OR i.category = :category) " +
                "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
                "AND (:maxPrice IS NULL OR i.price <= :maxPrice) " +
                "AND (:minRating IS NULL OR i.ratings >= :minRating)";
        TypedQuery<ProductDto> query = em.createQuery(jpql, ProductDto.class)
                .setParameter("category", category)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .setParameter("minRating", minRating)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        List<ProductDto> results = query.getResultList();
        // Fetch images for the items
        fetchAndSetImages(results);

        long total = countItemsWithFilters(category, minPrice, maxPrice, minRating);
        return new PageImpl<>(results, pageable, total);
    }

    private void fetchAndSetImages(List<ProductDto> products) {
        if (products.isEmpty()) {
            return;
        }

        List<Long> itemIds = products.stream().map(ProductDto::getId).collect(Collectors.toList());

        String imageJpql = "SELECT NEW test.shop.web.dto.ImageDto(img.id, img.url, img.item.id) " +
                "FROM Images img WHERE img.item.id IN :itemIds";
        List<ImageDto> images = em.createQuery(imageJpql, ImageDto.class)
                .setParameter("itemIds", itemIds)
                .getResultList();

        Map<Long, List<ImageDto>> imageMap = images.stream()
                .collect(Collectors.groupingBy(ImageDto::getId));

        products.forEach(product -> product.setImages(imageMap.getOrDefault(product.getId(), Collections.emptyList())));
    }

    private long countItemsWithFilters(Category category, Integer minPrice, Integer maxPrice, Integer minRating) {
        String jpql = "SELECT COUNT(i) FROM Item i " +
                "WHERE (:category IS NULL OR i.category = :category) " +
                "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
                "AND (:maxPrice IS NULL OR i.price <= :maxPrice) " +
                "AND (:minRating IS NULL OR i.ratings >= :minRating)";

        return em.createQuery(jpql, Long.class)
                .setParameter("category", category)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .setParameter("minRating", minRating)
                .getSingleResult();
    }
}
