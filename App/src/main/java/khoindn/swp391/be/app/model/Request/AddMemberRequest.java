package khoindn.swp391.be.app.model.Request;

import lombok.Data;
@Data
public class AddMemberRequest {
    private int userId;
    private String roleInGroup;
    private Float ownershipPercentage;
}
