package test.shop.web.form.item;

import lombok.Data;

@Data
public class MovieForm {

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    private String director;
    private String actor;
}
