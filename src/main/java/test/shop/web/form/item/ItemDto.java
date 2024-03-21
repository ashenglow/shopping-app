package test.shop.web.form.item;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import test.shop.domain.item.Item;

@Data
public class ItemDto {

        private Long id;
        private String name;
        private int price;
        private int stockQuantity;

        private String artist;
        private String etc;
        private String director;
        private String actor;
        private String author;
        private String isbn;


}
