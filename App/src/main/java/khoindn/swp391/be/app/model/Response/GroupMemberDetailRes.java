package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDetailRes {
    private int id;
    private String roleInGroup;
    private float ownershipPercentage;
    private String hovaten;
    private int userId;
    private int groupId;
}
