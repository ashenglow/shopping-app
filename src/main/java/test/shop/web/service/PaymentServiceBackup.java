package test.shop.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import test.shop.web.dto.request.PaymentApproveRequest;
import test.shop.web.dto.request.PaymentReadyRequest;
import test.shop.web.dto.response.ApproveResponse;
import test.shop.web.dto.response.ReadyResponse;

public class PaymentServiceBackup {
     private final RestTemplate restTemplate;
    private final OrderService orderService;

    public PaymentServiceBackup(RestTemplateBuilder builder, OrderService orderService) {
        this.restTemplate = builder.build();
        this.orderService = orderService;
    }


//    @Value("${kakaopay.api.secret.key}")
    @Value("DEVBF6C6B5AC953166285B884BC3378ACD52A42D")
    private String kakaopaySecretKey;

//    @Value("${kakaopay.cid")
    @Value("TC0ONETIME")
    private String cid;

//    @Value("${app.frontend.url}")
    @Value("http://localhost:3000")
    private String frontendUrl;
    @Value("T2345560999051526180")
    private String tid;
// static String partnerOrderId = "6406";
//    static String partnerUserId = "pg_qa";
//    static String paymentAid = "A2345561170850086930";
//    static String cancelAid = "A2345583027818929490";
//    static String pcConfirmationUrlPrefix = "https://pg-web.kakao.com/v1/confirmation/p/";
//    static String mobileConfirmationUrlPrefix = "https://pg-web.kakao.com/v1/confirmation/m/";
//    static String paymentAid = "A2345561170850086930";
//    static String cancelAid = "A2345583027818929490";
    public ReadyResponse ready(PaymentReadyRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", request.getTransactionId());
        parameters.add("partner_user_id", request.getUserId().toString());
        parameters.add("item_name", request.getItemName());
        parameters.add("quantity", String.valueOf(request.getQuantity()));
        parameters.add("total_amount", String.valueOf(request.getTotalAmount()));
        parameters.add("tax_free_amount", "0");
        parameters.add("vat_amount", "100");
        parameters.add("approval_url", frontendUrl + "/payment/approve");
        parameters.add("cancel_url", frontendUrl + "/payment/cancel");
        parameters.add("fail_url", frontendUrl + "/payment/fail");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<ReadyResponse> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/ready",
                entity,
                ReadyResponse.class);
        ReadyResponse readyResponse = response.getBody();
        if(readyResponse != null) {
            this.tid = readyResponse.getTid();
        }
        return readyResponse;
    }

    public ApproveResponse approve(PaymentApproveRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", this.tid);
        parameters.add("partner_order_id", request.getTransactionId());
        parameters.add("partner_user_id", request.getUserId().toString());
        parameters.add("pg_token", request.getPgToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<ApproveResponse> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/approve",
                entity,
                ApproveResponse.class);
        ApproveResponse approveResponse = response.getBody();
//        if(approveResponse != null) {
//            Long orderId = orderService.order(request.getUserId(), request.getOrderItems());
//            approveResponse.setOrderId(orderId);
//
//        }
        return approveResponse;
    }
}
