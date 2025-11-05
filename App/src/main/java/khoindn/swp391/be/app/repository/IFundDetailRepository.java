package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.FundDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFundDetailRepository extends JpaRepository<FundDetail, Integer> {

    // find all fund of a group in a month
    List<FundDetail> findByGroupMember_Group_GroupIdAndMonthYear(Integer groupId, String monthYear);

    // find all fund of 1 member
    List<FundDetail> findByGroupMember_Id(Integer groupMemberId);

    // find fund of 1 member in a month
    FundDetail findByGroupMember_IdAndMonthYear(Integer groupMemberId, String monthYear);



}
