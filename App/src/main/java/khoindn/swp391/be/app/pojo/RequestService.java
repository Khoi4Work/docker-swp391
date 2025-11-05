package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusRequestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RequestService {

    //attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status")
    private StatusRequestService status = StatusRequestService.PENDING ; // pending, in_progress, completed
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    //relationshipss

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "group_member_id")
    private GroupMember groupMember;

    @ManyToOne
    @JoinColumn(name = "service")
    private MenuVehicleService menuService;

}
