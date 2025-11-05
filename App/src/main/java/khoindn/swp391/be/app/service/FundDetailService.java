package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.model.Response.FundFeeResponse;
import khoindn.swp391.be.app.model.Response.GroupFeeResponse;
import khoindn.swp391.be.app.pojo.FundDetail;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo._enum.StatusFundDetail;
import khoindn.swp391.be.app.repository.IFundDetailRepository;
import khoindn.swp391.be.app.repository.IGroupMemberRepository;
import khoindn.swp391.be.app.repository.IGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class FundDetailService implements IFundDetailService {
    @Autowired
    private IFundDetailRepository fundDetailRepository;

    @Autowired
    private IGroupMemberRepository groupMemberRepository;

    @Autowired
    private IGroupRepository groupRepository;

    private static final BigDecimal DEFAULT_MONTHLY_FEE = new BigDecimal("1000000");
    private static final String TNM_CODE = "32AYQ5SY";
    private static final String SECRET_KEY = "8AE7U9X2Y8C26AHOM9NEVELW2MFCU7YR";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String RETURN_URL = "http://localhost:8080/api/fund-fee/payment-return";
    private static final int PAYMENT_DEADLINE_DAYS = 14; // days to pay before marking overdue

    //auto create monthly fee
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlyFeesForAllGroups() {
        String currentMonthYear = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        groupRepository.findAll().forEach(group -> {
            List<GroupMember> members = groupMemberRepository.findAllByGroup_GroupId(group.getGroupId());

            for (GroupMember member : members) {
                FundDetail existingFee = fundDetailRepository.findByGroupMember_IdAndMonthYear(
                        member.getId(),
                        currentMonthYear
                );

                if (existingFee == null) {
                    FundDetail monthlyFee = new FundDetail();
                    monthlyFee.setGroupMember(member);
                    monthlyFee.setAmount(DEFAULT_MONTHLY_FEE);
                    monthlyFee.setTransactionType("FEE");
                    monthlyFee.setMonthYear(currentMonthYear);
                    monthlyFee.setStatus(StatusFundDetail.PENDING);
                    monthlyFee.setIsOverdue(false);
                    monthlyFee.setDueDate(LocalDateTime.now().plusDays(PAYMENT_DEADLINE_DAYS));
                    fundDetailRepository.save(monthlyFee);
                }
            }
        });
    }

    @Override
    public String createPaymentUrl(Integer fundDetailId) throws Exception {
        FundDetail fundDetail = fundDetailRepository.findById(fundDetailId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        if (StatusFundDetail.COMPLETED.equals(fundDetail.getStatus())) {
            throw new RuntimeException("This fee has already been paid");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        String txnRef = fundDetail.getFundDetailId() + "_" + System.currentTimeMillis();
        BigDecimal amount = fundDetail.getAmount();

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", TNM_CODE);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toan phi thang " + fundDetail.getMonthYear());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal("100")).longValue()));
        vnpParams.put("vnp_ReturnUrl", RETURN_URL);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(SECRET_KEY, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(VNP_URL);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        return urlBuilder.toString();
    }


    @Override
    public GroupFeeResponse getGroupFeeDetails(Integer groupId) {
        String currentMonthYear = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<FundDetail> fees = fundDetailRepository.findByGroupMember_Group_GroupIdAndMonthYear(groupId, currentMonthYear);

        if (fees.isEmpty()) {
            throw new RuntimeException("No fees found for this group");
        }

        String groupName = fees.get(0).getGroupMember().getGroup().getGroupName();

        BigDecimal totalPending = fees.stream()
                .filter(f -> StatusFundDetail.PENDING.equals(f.getStatus()))
                .map(FundDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingCount = fees.stream()
                .filter(f -> StatusFundDetail.PENDING.equals(f.getStatus()))
                .count();

        long paidCount = fees.stream()
                .filter(f -> StatusFundDetail.COMPLETED.equals(f.getStatus()))
                .count();

        List<FundFeeResponse> feeResponses = fees.stream()
                .map(this::toFundFeeResponse)
                .collect(Collectors.toList());

        return new GroupFeeResponse(groupId, groupName, currentMonthYear, totalPending,
                (int) pendingCount, (int) paidCount, feeResponses);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void checkAndMarkOverdue() {
        LocalDateTime now = LocalDateTime.now();

        List<FundDetail> overdueFees = fundDetailRepository.findAll().stream()
                .filter(fee -> StatusFundDetail.PENDING.equals(fee.getStatus()))
                .filter(fee -> fee.getDueDate() != null && now.isAfter(fee.getDueDate()))
                .filter(fee -> !fee.getIsOverdue())
                .collect(Collectors.toList());
        overdueFees.forEach(fee -> {
            fee.setIsOverdue(true);
            fundDetailRepository.save(fee);


        });
    }

    @Override
    public List<FundFeeResponse> getUserOverdueFeesL(Integer userId) {
        List<GroupMember> groupMembers = groupMemberRepository.findAllByUsersId(userId);

        return groupMembers.stream()
                .flatMap(gm -> fundDetailRepository.findByGroupMember_Id(gm.getId()).stream())
                .filter(fee -> fee.getIsOverdue() && StatusFundDetail.PENDING.equals(fee.getStatus()))
                .map(this::toFundFeeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FundFeeResponse getFeeByGroupMemberIdCurrentMonth(Integer groupMemberId) {
        String currentMonthYear = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        FundDetail fee = fundDetailRepository.findByGroupMember_IdAndMonthYear(
                groupMemberId,
                currentMonthYear);
        return toFundFeeResponse(fee);
    }

    @Override
    @Transactional
    public void payFeeDirectly(Integer fundDetailId) {
        FundDetail fundDetail = fundDetailRepository.findById(fundDetailId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        if (StatusFundDetail.COMPLETED.equals(fundDetail.getStatus())) {
            throw new RuntimeException("This fee has already been paid");
        }

        fundDetail.setStatus(StatusFundDetail.COMPLETED);
        fundDetail.setIsOverdue(false);
        fundDetailRepository.save(fundDetail);
    }


    // ===== HELPER - HMAC SHA-512 =====
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

    private FundFeeResponse toFundFeeResponse(FundDetail fee) {
        FundFeeResponse response = new FundFeeResponse();
        response.setFundDetailId(fee.getFundDetailId());
        response.setGroupMemberId(fee.getGroupMember().getId());
        response.setUserId(fee.getGroupMember().getUsers().getId());
        response.setUserName(fee.getGroupMember().getUsers().getHovaTen());
        response.setAmount(fee.getAmount());
        response.setMonthYear(fee.getMonthYear());
        response.setStatus(fee.getStatus());
        response.setIsOverdue(fee.getIsOverdue());
        response.setDueDate(fee.getDueDate());
        response.setCreatedAt(fee.getCreatedAt());
        return response;
    }
}
