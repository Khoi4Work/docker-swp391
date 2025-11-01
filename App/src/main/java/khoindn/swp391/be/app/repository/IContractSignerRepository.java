package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Contract;
import khoindn.swp391.be.app.pojo.ContractSigner;
import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IContractSignerRepository extends JpaRepository<ContractSigner, Integer> {
    ContractSigner findByUser_IdAndContract_ContractId(int userId, int contractId);

    boolean existsByUser_Id(int userId);

    List<ContractSigner> findByContract_ContractId(int contractContractId);

    ContractSigner findContractSignerByContract_ContractId(int contractContractId);

    List<ContractSigner> findAllByContract_ContractId(int contractContractId);

    ContractSigner findContractSignerByUser(Users user);
}
