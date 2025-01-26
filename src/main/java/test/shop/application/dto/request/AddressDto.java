package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.value.Address;

@Data
@Builder
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private Address address;
}
