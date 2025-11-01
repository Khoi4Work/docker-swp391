package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.CommonFund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICommonFundRepository extends JpaRepository<CommonFund, Integer> {
    CommonFund findByFundId(int fundId);

    CommonFund findByGroupGroupId(int groupId);

}
