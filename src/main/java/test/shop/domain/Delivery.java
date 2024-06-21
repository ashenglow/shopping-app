package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // [READY 준비, COMP 배송]

    @Embedded
    private Address address;

    public void saveOrder(Order order) {
        this.order = order;
    }

    public void saveStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void saveAddress(Address address) {
        this.address = address;
    }
}
