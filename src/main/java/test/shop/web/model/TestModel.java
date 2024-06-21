package test.shop.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class TestModel {
    private Long id;
    private String name;
    private String description;
    private int price;
    private int ratings;
    private String category;
    private int stock;
    private int numOfReviews;

    public TestModel() {
    }

    public TestModel(Long id, String name, String description, int price, int ratings, String category, int stock, int numOfReviews) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ratings = ratings;
        this.category = category;
        this.stock = stock;
        this.numOfReviews = numOfReviews;
    }
}
