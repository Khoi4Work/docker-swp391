package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.BalanceNotEnoughException;
import khoindn.swp391.be.app.exception.exceptions.CommonFundNotFoundException;
import khoindn.swp391.be.app.exception.exceptions.GroupMemberNotFoundException;
import khoindn.swp391.be.app.model.Request.WithdrawRequest;
import khoindn.swp391.be.app.pojo.CommonFund;
import khoindn.swp391.be.app.pojo.FundDetail;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo._enum.StatusFundDetail;
import khoindn.swp391.be.app.repository.ICommonFundRepository;
import khoindn.swp391.be.app.repository.IFundDetailRepository;
import khoindn.swp391.be.app.repository.IGroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private IFundDetailRepository fundDetailRepository;

    @Autowired
    private ICommonFundRepository commonFundRepository;

    @Autowired
    private IGroupMemberRepository groupMemberRepository;

    public String createPaymentURL(Integer fundId, Integer groupId, Integer userId, BigDecimal amount) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        String tmnCode = "32AYQ5SY";
        String secretKey = "8AE7U9X2Y8C26AHOM9NEVELW2MFCU7YR";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://localhost:8080/api/fund-payment/success/" + fundId + "/" + groupId + "/" + userId;

        String txnRef = UUID.randomUUID().toString().substring(0, 8);

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Nap tien vao quy - Fund: " + fundId + " - User: " + userId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal("100")).longValue()));
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public void processSuccessfulPayment(Integer fundId, Integer groupId, Integer userId, BigDecimal amount) {
        // find group member
        GroupMember groupMember = groupMemberRepository.findByGroupGroupIdAndUsersId(groupId, userId);
        if (groupMember == null) {
            throw new GroupMemberNotFoundException("Group member not found");
        }
        // find common fund
        CommonFund commonFund = commonFundRepository.findByFundId(fundId);
        if (commonFund == null) {
            throw new CommonFundNotFoundException("Common fund not found");
        }
        FundDetail fundDetail = new FundDetail();
        fundDetail.setAmount(amount);
        fundDetail.setCommonFund(commonFund);
        fundDetail.setTransactionType("DEPOSIT");
        fundDetail.setStatus(StatusFundDetail.COMPLETED);
        fundDetail.setGroupMember(groupMember);
        fundDetailRepository.save(fundDetail);
        // update balance
        commonFund.setBalance(commonFund.getBalance().add(amount));
        commonFundRepository.save(commonFund);
    }

    public Integer withdrawMoney(WithdrawRequest request) {
        GroupMember groupMember = groupMemberRepository.findByGroupGroupIdAndUsersId(
                request.getGroupId(),
                request.getUserId()
        );

        if (groupMember == null) {
            throw new RuntimeException("Group member not found");
        }


        CommonFund commonFund = commonFundRepository.findByFundId(request.getFundId());
        if (commonFund == null) {
            throw new RuntimeException("Fund not found");
        }

        BigDecimal currentBalance = commonFund.getBalance();
        if (currentBalance.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: " + currentBalance);
        }

        FundDetail fundDetail = new FundDetail();
        fundDetail.setCommonFund(commonFund);
        fundDetail.setGroupMember(groupMember);
        fundDetail.setAmount(request.getAmount());
        fundDetail.setTransactionType("WITHDRAW");
        fundDetail.setStatus(StatusFundDetail.COMPLETED);
        fundDetail = fundDetailRepository.save(fundDetail);

        commonFund.setBalance(currentBalance.subtract(request.getAmount()));
        commonFundRepository.save(commonFund);

        return fundDetail.getFundDetailId();
    }

    public CommonFund getCommonFundByGroupId(int groupId) {
        return commonFundRepository.findByGroupGroupId(groupId);
    }

    public CommonFund getCommonFundById(int fundId) {
        return commonFundRepository.findByFundId(fundId);
    }

    public List<FundDetail> getFundDetailById(int fundDetailId) {
        return fundDetailRepository.findByCommonFund_FundId(fundDetailId);
    }

}
