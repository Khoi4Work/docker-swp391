package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusRequestServiceDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RequestVehicleServiceDetail {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status")
    private StatusRequestServiceDetail status = StatusRequestServiceDetail.PENDING; // pending, in_progress, completed
    @Column(name = "description")
    private String description;
    // Relationships
    @OneToOne(mappedBy = "requestVehicleServiceDetail")
    private RequestVehicleService requestVehicleService;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Users staff;
}
