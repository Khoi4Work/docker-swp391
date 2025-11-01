package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllGroupsOfMember {
    private String roleInGroup;
    private String status;
    private float ownershipPercentage;

    private Group group;
    private List<GroupMember> members;
//    private List<RequestGroupService> requestGroups = new ArrayList<>();
}
