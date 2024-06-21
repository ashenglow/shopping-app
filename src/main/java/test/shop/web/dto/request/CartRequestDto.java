package test.shop.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import test.shop.web.dto.ItemDto;

@Data
@AllArgsConstructor
public class CartRequestDto implements UnifiedRequestDto {
    private Long memberId;
    private int count;
    private ItemDto itemDto;
}
