package test.shop.domain.model.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import test.shop.application.dto.response.ResponseImageDto;
import test.shop.domain.model.review.Review;
import test.shop.domain.model.exception.NotEnoughStockException;
import test.shop.application.dto.response.ProductDetailDto;
import test.shop.application.dto.request.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@Entity
@Getter
@Table(name = "items", indexes = {
        @Index(name = "idx_category_ratings", columnList = "category, ratings")
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private String description;
    private int stockQuantity;
    private double ratings;
    private int numOfReviews;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Formula("(SELECT i.url FROM images i WHERE i.item_id = item_id ORDER BY i.display_order LIMIT 1)")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<Image> images = new ArrayList<>();

    @Builder
    public Item(String name, int price, int stockQuantity, String description, double ratings, int numOfReviews, Category category){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.ratings = ratings;
        this.numOfReviews = numOfReviews;
        this.category = category;

    }

    public void modifyPrice(int price) {
        this.price = price;
    }

    // Stock management methods
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }


    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

    // Review methods

    public void updateReviewStats(){
        this.numOfReviews = this.reviews.size();
        this.ratings = this.reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public void addReview(Review review) {
        if(!this.reviews.contains(review)){
            this.reviews.add(review);
            if(review.getItem() != null){
                review.saveItem(this);
            }
            updateReviewStats();
        }
    }



    public void saveCategory(Category category) {
        this.category = category;
    }

    public void saveImages(Image image) {
        this.images.add(image);
    }

    public void saveAllImages(List<Image> images) {
        this.images.addAll(images);
    }

    // Optimized method to set thumbnail
    public void setThumbnailUrlFromImages(){
        if (!images.isEmpty()){
            this.thumbnailUrl = images.get(0).getUrl();
        }
    }

    // Image management
    public void addImage(String url){
        Image image = Image.builder()
                .url(url)
                .displayOrder(images.size())
                .build();
        image.setItem(this);
        images.add(image);

    }

    public void addImages(List<String> urls){
       urls.forEach(this::addImage);
    }

    public void clearImages(){
        images.clear();
    }

    private void updateRatings() {
        this.numOfReviews = reviews.size();
        if(numOfReviews > 0) {
            this.ratings = (double) Math.round(reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0));
        } else {
            this.ratings = 0;
        }
    }


    public void reorderImage(Long imageId, int newPosition) {
        if(newPosition < 0 || newPosition >= images.size()){
            throw new IllegalArgumentException("Invalid position: " + newPosition);
        }

        Image imageToMove = images.stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        images.remove(imageToMove);
        images.add(newPosition, imageToMove);

        //update all display orders
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setDisplayOrder(i);
        }
    }

    public void removeImage(Long imageId){
        images.removeIf(img -> img.getId().equals(imageId));
        //update remaining display orders
        for(int i = 0; i < images.size(); i++){
            images.get(i).setDisplayOrder(i);
        }
    }

    public String getThumbnailUrl() {
        return images.isEmpty() ? null : images.get(0).getUrl();
    }

    public void updateThumbnail(String url){
        boolean urlExists = images.stream()
                .anyMatch(img -> img.getUrl().equals(url));

        if( urlExists) {
            this.thumbnailUrl = url;
        } else {
            throw new IllegalArgumentException("Image URL not found in product images");
        }
    }

    // DTO related Methods

    public void updateFromDto(ProductDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stockQuantity = dto.getStockQuantity();
        this.description = dto.getDescription();
        this.category = dto.getCategory();
        this.ratings = dto.getRatings();
        this.numOfReviews = dto.getNumOfReviews();

        // Clear existing images and add new ones
        this.images.clear();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            dto.getImages().forEach(imgDto -> this.addImage(imgDto.getUrl()));
        }
    }
    @Override
    public String toString() {
        return String.format("Item{id=%d, name='%s', category=%s, images=%d}",
                id, name, category, images.size());
    }


    public ProductDetailDto toProductDetailDto() {
        return ProductDetailDto.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .description(this.description)
                .ratings(this.ratings)
                .numOfReviews(this.numOfReviews)
                .category(this.category)
                .reviews(this.reviews.stream()
                        .map(Review::toReviewDto)
                        .collect(Collectors.toList()))
                .images(this.images.stream()
                        .map(Image::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public ProductDto toProductDto() {
        return ProductDto.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .description(this.description)
                .ratings(this.ratings)
                .numOfReviews(this.numOfReviews)
                .category(this.category)
                .images(this.images.stream()
                        .map(Image::toDto)
                        .collect(Collectors.toList())
                )
                .build();
    }


}
