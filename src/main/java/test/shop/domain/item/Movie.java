package test.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("M")
@Getter
@SuperBuilder
@RequiredArgsConstructor
public class Movie extends Item {

    private String director;
    private String actor;


}
