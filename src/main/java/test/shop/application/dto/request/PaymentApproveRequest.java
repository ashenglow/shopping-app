package test.shop.application.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
public class PaymentApproveRequest {
    private String transactionId;
    private Long userId;
    private String pgToken;

}
