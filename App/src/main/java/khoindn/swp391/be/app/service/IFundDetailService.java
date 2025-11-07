package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Response.FundFeeResponse;
import khoindn.swp391.be.app.model.Response.GroupFeeResponse;
import khoindn.swp391.be.app.pojo.FundDetail;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;

public interface IFundDetailService {
    void generateMonthlyFeesForAllGroups();

    String createPaymentUrl(Integer fundDetailId) throws Exception;

    void payFeeDirectly(Integer fundDetailId);

    GroupFeeResponse getGroupFeeDetails(Integer groupId);

    void checkAndMarkOverdue();

    List<FundFeeResponse> getUserOverdueFeesL(Integer userId);

    FundFeeResponse getFeeByGroupMemberIdCurrentMonth(Integer groupMemberId);

    FundDetail findById(int id);

    FundDetail addFund(FundDetail fundDetail);


}
