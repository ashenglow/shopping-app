package test.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import test.shop.web.form.item.BookForm;

@NoArgsConstructor
@Entity
@Getter
@Setter
@DiscriminatorValue("B")
@SuperBuilder
public class Book extends Item {
    private String author;
    private String isbn;

    public Book(String name, int price, int stockQuantity, String author, String isbn) {
        super(name, price, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }

    public void updateBook(String name, int price, int stockQuantity, String author, String isbn) {
        super.updateItem(name, price, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }
}
