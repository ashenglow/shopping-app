package test.shop.web.controller.restcontroller;

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
//@RequestMapping("/api/auth/v1/payment")
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/api/auth/v1/payment/ready")
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
