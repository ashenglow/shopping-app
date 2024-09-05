package test.shop.web.controller.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.web.dto.request.PaymentApproveRequest;
import test.shop.web.dto.request.PaymentReadyRequest;
import test.shop.web.dto.response.ApproveResponse;
import test.shop.web.dto.response.ReadyResponse;
import test.shop.web.service.PaymentService;

@RestController
@RequiredArgsConstructor
@Tag(name = "결제", description = "결제 API 입니다")
//@RequestMapping("/api/auth/v1/payment")
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/api/auth/v1/payment/ready")
    @Operation(summary = "결제 준비", description = "클라이언트가 결제를 요청합니다.")
    public ResponseEntity<ReadyResponse> ready(@RequestParam("partner_order_id") String partnerOrderId,  @RequestBody PaymentReadyRequest request) {
        ReadyResponse response = paymentService.ready(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/public/v1/payment/ready-test")
    public ResponseEntity<ReadyResponse> ready_test(@RequestParam("partner_order_id") String partnerOrderId,  @RequestBody PaymentReadyRequest request) {
        ReadyResponse response = paymentService.ready(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/v1/payment/approve")
    @Operation(summary = "결제 요청", description = "페이 서버에 결제를 요청합니다.")
    public ResponseEntity<ApproveResponse> approve(@RequestParam("partner_order_id") String partnerOrderId, @RequestParam("pg_token") String pgToken, @RequestBody PaymentApproveRequest request) {
        ApproveResponse response = paymentService.approve(pgToken, partnerOrderId, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/public/v1/payment/approve-test")
    public ResponseEntity<ApproveResponse> approve_test(@RequestParam("partner_order_id") String partnerOrderId, @RequestParam("pg_token") String pgToken, @RequestBody PaymentApproveRequest request) {
        ApproveResponse response = paymentService.approve(pgToken, partnerOrderId, request);
        return ResponseEntity.ok(response);
    }


}
