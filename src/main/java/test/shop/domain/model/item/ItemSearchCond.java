package test.shop.domain.model.item;

import lombok.Data;

@Data
public class ItemSearchCond {
    private int price;
    private double ratings;
    private String category;
}
