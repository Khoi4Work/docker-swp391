package khoindn.swp391.be.app.model.Response;

import khoindn.swp391.be.app.pojo.Group;
import khoindn.swp391.be.app.pojo.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private int id;
    private Group groupId;
    private Users userId;
    private String roleInGroup;
    private String status;
    private LocalDateTime createdAt;
    private float ownershipPercentage;
}
