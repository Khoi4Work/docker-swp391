package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Contract;
import khoindn.swp391.be.app.pojo.ContractSigner;
import khoindn.swp391.be.app.pojo._enum.StatusContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IContractRepository extends JpaRepository<Contract, Integer> {
    Contract findContractByContractId(int contractId);

    Contract findContractByGroup_GroupId(int groupGroupId);

    List<Contract> getContractsByStatus(StatusContract status);
}
