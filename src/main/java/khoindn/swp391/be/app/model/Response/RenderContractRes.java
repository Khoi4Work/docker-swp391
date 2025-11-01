package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo.Contract;
import khoindn.swp391.be.app.pojo.ContractSigner;
import khoindn.swp391.be.app.pojo.GroupMember;
import khoindn.swp391.be.app.pojo.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenderContractRes {
    GroupMember ownerMember;
    List<GroupMember> coOwnerMembers;
    Vehicle vehicle;
    List<ContractSigner> contracts;

}
