package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import khoindn.swp391.be.app.model.Response.*;
import khoindn.swp391.be.app.pojo.FundDetail;
import khoindn.swp391.be.app.repository.IFundDetailRepository;
import khoindn.swp391.be.app.service.IFundDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fund-fee")
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "http://localhost:8081")
public class FundDetailController {

    @Autowired
    private IFundDetailService fundDetailService;
    @Autowired
    private IFundDetailRepository fundDetailRepository;

    // ===== 1. TẠO PAYMENT URL VNPAY =====
    @PostMapping("/{fundDetailId}/create-payment")
    public ResponseEntity<FundPaymentResponse> createPayment(@PathVariable Integer fundDetailId) throws Exception {
        String paymentUrl = fundDetailService.createPaymentUrl(fundDetailId);
        return ResponseEntity.ok(new FundPaymentResponse(
                "SUCCESS",
                "Payment URL created successfully",
                paymentUrl
        ));
    }

    // ===== 2. CALLBACK VNPAY =====
    @GetMapping("/payment-return")
    public void paymentReturn(
            @RequestParam Map<String, String> allParams,
            HttpServletResponse response) throws Exception {

        String vnpResponseCode = allParams.get("vnp_TransactionStatus");
        boolean isSuccess = "00".equals(vnpResponseCode);

        String redirectUrl = "http://localhost:8081/co-owner/payment-failed";

        if (isSuccess) {
            String txnRef = allParams.get("vnp_TxnRef");
            Integer fundDetailId = Integer.parseInt(txnRef.split("_")[0]);
            fundDetailService.payFeeDirectly(fundDetailId);

            redirectUrl = "http://localhost:8081/co-owner/payment-success";
        }

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/set-overdue/{feeId}")
    public ResponseEntity<String> setOverdueImmediately(@PathVariable Integer feeId) {
        fundDetailRepository.findById(feeId).ifPresentOrElse(
                fee -> {
                    fee.setIsOverdue(true);
                    fundDetailRepository.save(fee);
                    System.out.println("Fee " + feeId + " marked as overdue");
                },
                () -> {
                    throw new RuntimeException("Fee not found");
                }
        );
        return ResponseEntity.ok("Fee " + feeId + " is now overdue");
    }

    @GetMapping("/group/{groupId}/current-month")
    public ResponseEntity<GroupFeeResponse> getGroupFeeDetails(
            @PathVariable Integer groupId) {
        GroupFeeResponse response = fundDetailService.getGroupFeeDetails(groupId);
        return ResponseEntity.ok(response);
    }


    // api testing
    @PostMapping("/generate-test")
    public ResponseEntity<?> generateFeesTest() {
        fundDetailService.generateMonthlyFeesForAllGroups();
        return ResponseEntity.ok("Fees generated for testing");
    }

    @GetMapping("/group-member/{groupMemberId}/current-month")
    public ResponseEntity<FundFeeResponse> getFeeByGroupMemberIdCurrentMonth(@PathVariable Integer groupMemberId) {
        FundFeeResponse fee = fundDetailService.getFeeByGroupMemberIdCurrentMonth(groupMemberId);
        return ResponseEntity.ok(fee);
    }
}
