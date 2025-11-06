package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.ContractCreateReq;
import khoindn.swp391.be.app.model.Request.ContractDecisionReq;
import khoindn.swp391.be.app.model.Response.ContractHistoryRes;
import khoindn.swp391.be.app.model.Response.ContractPendingRes;
import khoindn.swp391.be.app.pojo.Contract;
import khoindn.swp391.be.app.pojo.ContractSigner;
import khoindn.swp391.be.app.pojo.Users;

import java.util.List;

public interface IContractService {
    Contract getContractByContractId(int id);

    ContractSigner setContract(ContractDecisionReq req)
            throws
            Exception;


    List<ContractSigner> createContract(ContractCreateReq req) throws Exception;

    List<ContractHistoryRes> getHistoryContractsByUser(Users user);

    List<ContractSigner> getAllContractSignersByContractId(int id);

    List<ContractSigner> getContractSignerByContractId(int id);

    List<ContractPendingRes> getPendingContracts();

    void SendWaitingConfirmedContract(int contractId);

    void sendDeclinedContractNotification(int contractId) throws Exception;

    void verifyContract(int contractId, int decision, Users staff) throws Exception;

}
