package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusRequestGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestGroup {

    //attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameRequestGroup;
    private String descriptionRequestGroup = "No description";
    private StatusRequestGroup status = StatusRequestGroup.PENDING; // pending, approved, rejected
    private LocalDateTime createdAt = LocalDateTime.now();

    //relationships
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "groupMember_id")
    private GroupMember groupMember;

    @OneToOne(mappedBy = "requestGroup", cascade = CascadeType.ALL)
    private RequestGroupDetail requestGroupDetail;


}
