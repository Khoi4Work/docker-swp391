package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import khoindn.swp391.be.app.model.Request.PaymentRequest;
import khoindn.swp391.be.app.model.Request.WithdrawRequest;
import khoindn.swp391.be.app.model.Response.PaymentResponse;
import khoindn.swp391.be.app.model.Response.WithdrawResponse;
import khoindn.swp391.be.app.pojo.CommonFund;
import khoindn.swp391.be.app.pojo.FundDetail;
import khoindn.swp391.be.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fund-payment")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")

public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) throws Exception {
        String paymentUrl = paymentService.createPaymentURL(paymentRequest.getFundId(),
                paymentRequest.getGroupId(),
                paymentRequest.getUserId(),
                paymentRequest.getAmount()
        );
        return ResponseEntity.ok(new PaymentResponse(paymentUrl, "Payment URL created successfully"));
    }

    @GetMapping("/success/{fundId}/{groupId}/{userId}")
    public ResponseEntity paymentSuccess(
            @PathVariable Integer fundId,
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @RequestParam Map<String, String> allParams,
            HttpServletResponse response) throws Exception {

        String vnpResponseCode = allParams.get("vnp_TransactionStatus");
        boolean isValid = "00".equals(vnpResponseCode);
        //FE Page
        String redirectUrl = "http://localhost:8081/co-owner/payment-failed";

        if (isValid) {
            String amountStr = allParams.get("vnp_Amount");
            BigDecimal amount = new BigDecimal(amountStr).divide(new BigDecimal("100"));

            paymentService.processSuccessfulPayment(fundId, groupId, userId, amount);

            redirectUrl = "http://localhost:8081/co-owner/payment-success?amount=&gidzl=5rEy2PGLgqCNJxiBbGgYH1HFiGsE2gGN0n_aMOSAeKS6JxC9WGYW45H5v5xILQ1501UnMJEqT25_a1UiGG";
        }

        response.sendRedirect(redirectUrl);
        return ResponseEntity.ok("Payment processed");
    }

    @GetMapping("/common-fund/group/{groupId}")
    public ResponseEntity<CommonFund> getCommonFundByGroupId(@PathVariable Integer groupId) {
        return ResponseEntity.ok(paymentService.getCommonFundByGroupId(groupId));
    }

    @GetMapping("/common-fund/{fundId}")
    public ResponseEntity<CommonFund> getCommonFundById(@PathVariable Integer fundId) {
        return ResponseEntity.ok(paymentService.getCommonFundById(fundId));
    }

    @GetMapping("/fund-details/{fundId}")
    public ResponseEntity<List<FundDetail>> getFundDetailsByFundId(@PathVariable Integer fundId) {
        return ResponseEntity.ok(paymentService.getFundDetailById(fundId));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdrawMoney(@RequestBody WithdrawRequest request) {
        Integer transactionId = paymentService.withdrawMoney(request);

        return ResponseEntity.ok(new WithdrawResponse(
                "SUCCESS",
                "Withdraw successful",
                transactionId
        ));
    }
}
