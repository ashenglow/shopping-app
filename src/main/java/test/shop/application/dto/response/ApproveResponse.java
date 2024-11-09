package test.shop.application.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private Amount amount;
    private String createdAt;
    private String approvedAt;

//    private Long orderId;

    @Data
    public static class Amount{
        private Integer total;
        private Integer taxFree;
        private Integer vat;
        private Integer point;
        private Integer discount;
        private Integer greenDeposit;
    }
}
