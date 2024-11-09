package test.shop.domain.model.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.application.dto.response.ResponseImageDto;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;

@Entity
@Data
@NoArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;

    @Column(name = "display_order")
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public Image(String url, int displayOrder) {
        this.url = url;
        this.displayOrder = displayOrder;
    }


    public void saveItem(Item item) {
        this.item = item;
        if(!item.getImages().contains(this)){
            item.getImages().add(this);
        }
    }


    public ResponseImageDto toDto(){
        return ResponseImageDto.builder()
                .id(this.id)
                .url(this.url)
                .displayOrder(this.displayOrder)
                .productId(this.item != null ? this.item.getId() : null)
                .build();
    }

}
