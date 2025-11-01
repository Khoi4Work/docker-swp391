package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.FundDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFundDetailRepository extends JpaRepository<FundDetail, Integer> {
    List<FundDetail> findByCommonFund_FundId(int fundId);
}
