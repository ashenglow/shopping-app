package test.shop.web.form.item;

import lombok.*;
import test.shop.domain.item.Book;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class BookForm {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;

    public BookForm(Long id, String name, int price, int stockQuantity, String author, String isbn) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.author = author;
        this.isbn = isbn;
    }
}
