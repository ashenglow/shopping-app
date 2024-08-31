package test.shop.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import test.shop.web.dto.request.ApproveRequest;
import test.shop.web.dto.request.PaymentApproveRequest;
import test.shop.web.dto.request.PaymentReadyRequest;
import test.shop.web.dto.request.ReadyRequest;
import test.shop.web.dto.response.ApproveResponse;
import test.shop.web.dto.response.ReadyResponse;

@Service
@Slf4j
public class PaymentService {

    private final OrderService orderService;

    public PaymentService(OrderService orderService) {

        this.orderService = orderService;
    }


    @Value("${kakaopay.api.secret.key}")
    private String kakaopaySecretKey;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private String tid;

    public ReadyResponse ready(PaymentReadyRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "DEV_SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//        parameters.add("cid", cid);
//        parameters.add("partner_order_id", request.getTransactionId());
//        parameters.add("partner_user_id", request.getUserId().toString());
//        parameters.add("item_name", request.getItemName());
//        parameters.add("quantity", String.valueOf(request.getQuantity()));
//        parameters.add("total_amount", String.valueOf(request.getTotalAmount()));
//        parameters.add("tax_free_amount", "0");
//        parameters.add("vat_amount", "100");
//        parameters.add("approval_url", frontendUrl + "/payment/approve");
//        parameters.add("cancel_url", frontendUrl + "/payment/cancel");
//        parameters.add("fail_url", frontendUrl + "/payment/fail");

        ReadyRequest readyRequest = ReadyRequest.builder()
                .cid(cid)
                .partnerOrderId(request.getTransactionId())
                .partnerUserId(request.getUserId().toString())
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .totalAmount(request.getTotalAmount())
                .taxFreeAmount(0)
                .vatAmount(null)
                .approvalUrl(frontendUrl + "/payment/approve?partner_order_id=" + request.getTransactionId())
                .cancelUrl(frontendUrl + "/payment/cancel")
                .failUrl(frontendUrl + "/payment/fail")
                .build();

        //send request
        HttpEntity<ReadyRequest> entityMap = new HttpEntity<>(readyRequest, headers);
        ResponseEntity<ReadyResponse> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                entityMap,
                ReadyResponse.class
        );
        ReadyResponse readyResponse = response.getBody();

        //주문번호 partner_order_id 와 TID 매핑해서 저장
        this.tid = readyResponse.getTid();
        return readyResponse;
    }

    public ApproveResponse approve(String pgToken, String partnerOrderId, PaymentApproveRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("tid : " + this.tid);
//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//        parameters.add("cid", cid);
//        parameters.add("tid", this.tid);
//        parameters.add("partner_order_id", request.getTransactionId());
//        parameters.add("partner_user_id", request.getUserId().toString());
//        parameters.add("pg_token",pgToken);
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        ApproveRequest approveRequest = ApproveRequest.builder()
                .cid(cid)
                .tid(getTidByPartnerOrderId(partnerOrderId))
                .partnerOrderId(partnerOrderId)
                .partnerUserId(request.getUserId().toString())
                .pgToken(pgToken)
                .build();
        HttpEntity<ApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);

        ResponseEntity<ApproveResponse> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                entityMap,
                ApproveResponse.class);
        ApproveResponse approveResponse = response.getBody();
//        if(approveResponse != null) {
//            Long orderId = orderService.order(request.getUserId(), request.getOrderItems());
//            approveResponse.setOrderId(orderId);
//
//        }
        return approveResponse;


    }

    private String getTidByPartnerOrderId(String partnerOrderId) {
        return this.tid;
    }


}
