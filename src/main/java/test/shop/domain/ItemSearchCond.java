package test.shop.domain;

import lombok.Data;

@Data
public class ItemSearchCond {
    private int price;
    private int ratings;
    private String category;
}
