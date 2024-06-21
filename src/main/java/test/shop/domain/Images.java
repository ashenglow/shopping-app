package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.domain.item.Item;
import test.shop.web.dto.ImageDto;

@Entity
@Getter
@NoArgsConstructor
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;

    public Images(String url) {
        this.url = url;
    }

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public void saveItem(Item item) {
        this.item = item;
        item.saveImages(this);
    }

    public static Images createImages(String url, Item item) {
        Images images = new Images(url);
        images.saveItem(item);
        return images;
    }

    public static ImageDto newImageDto(Images images) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(images.getId());
        imageDto.setUrl(images.getUrl());
        imageDto.setProductId(images.getItem().getId());
        return imageDto;

    }
}
