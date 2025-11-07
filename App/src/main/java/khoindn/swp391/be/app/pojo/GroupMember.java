package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusGroupMember;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    // attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private int id;
    @Column(name = "role_in_group")
    private String roleInGroup;
    @Column(name = "status")
    private StatusGroupMember status = StatusGroupMember.ACTIVE;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "ownership_percentage")
    private float ownershipPercentage;
    // relationship
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @OneToMany(mappedBy = "groupMember")
    @JsonIgnore
    @ToString.Exclude
    private List<DecisionVoteDetail> decisionVoteDetails = new ArrayList<>();

    @OneToMany(mappedBy = "groupMember", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<RequestGroupService> requestGroupServices = new ArrayList<>();

    @OneToMany(mappedBy = "groupMember", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<FundDetail> fundDetails = new ArrayList<>();

    @OneToMany(mappedBy = "groupMember", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<RequestVehicleService> requestVehicleServices = new ArrayList<>();
}
