package test.shop.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String baseAddress;
    private String detailAddress;
    private String zipcode;

    protected Address() {

    }

    public Address(String zipcode, String baseAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }

   public String getFullAddress() {
        if( detailAddress == null || detailAddress.trim().isEmpty()) {
            return baseAddress;
        }
        return baseAddress + ", " + detailAddress;
   }
}
