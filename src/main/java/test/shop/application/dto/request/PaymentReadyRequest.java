package test.shop.application.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
public class PaymentReadyRequest {
    private String transactionId;
    private Long userId;
    private String itemName;
    private Integer quantity;
    private Integer totalAmount;
}
