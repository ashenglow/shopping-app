package test.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("B")
@Getter
@SuperBuilder
@RequiredArgsConstructor
public class Book extends Item {
    private String author;
    private String isbn;


}
