package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import test.shop.application.dto.response.ItemDto;

@Data
@AllArgsConstructor
public class CartRequestDto implements UnifiedRequestDto {
    private Long memberId;
    private int count;
    private ItemDto itemDto;
}
