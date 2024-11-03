package test.shop.domain.model.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.domain.model.review.Review;
import test.shop.domain.model.exception.NotEnoughStockException;
import test.shop.application.dto.response.ItemDto;
import test.shop.application.dto.response.ProductDetailDto;
import test.shop.application.dto.request.ProductDto;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Entity
@Getter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private String description;
    private int stockQuantity;
    private int ratings;
    private int numOfReviews;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> images = new ArrayList<>();

    public void modifyPrice(int price) {
        this.price = price;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void saveCategory(Category category) {
        this.category = category;
    }

    public void saveImages(Images images) {
        this.images.add(images);
    }

    public void saveAllImages(List<Images> images) {
        this.images.addAll(images);
    }
    public void saveTestImages() {
        Images images = Images.createImages("https://placehold.co/600x400/png", this);
        this.images.add(images);
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
@Builder
    public Item(String name, int price, int stockQuantity, String description, int ratings, int numOfReviews, Category category){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.ratings = ratings;
        this.numOfReviews = numOfReviews;
        this.category = category;
        saveTestImages();
    }

    public void saveItem(ProductDto form) {
        this.name = form.getName();
        this.price = form.getPrice();
        this.stockQuantity = form.getStockQuantity();
        this.description = form.getDescription();
        this.ratings = form.getRatings();
        this.numOfReviews = form.getNumOfReviews();
        this.category = form.getCategory();
        saveTestImages();
    }

    public void updateItem(String name, int price, int stockQuantity, String description, int ratings, int numOfReviews, Category category){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.ratings = ratings;
        this.numOfReviews = numOfReviews;
        this.category = category;
    }

    public ProductDetailDto newProductDetailDto(Item item) {
        return ProductDetailDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .description(item.getDescription())
                .ratings(item.getRatings())
                .numOfReviews(item.getNumOfReviews())
                .category(item.getCategory())
                .reviews(item.getReviews().stream().map(Review::newReviewDto).toList())
                .images(item.getImages().stream().map(Images::newImageDto).toList())
                .build();
    }

    public static ProductDto newProductDto(Item item) {
        return ProductDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .description(item.getDescription())
                .ratings(item.getRatings())
                .numOfReviews(item.getNumOfReviews())
                .category(item.getCategory())
                .images(item.getImages().stream().map(Images::newImageDto).toList())
                .build();
    }

    public static ItemDto newItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .images(item.getImages().stream().map(Images::newImageDto).toList())
                .build();
    }

}
