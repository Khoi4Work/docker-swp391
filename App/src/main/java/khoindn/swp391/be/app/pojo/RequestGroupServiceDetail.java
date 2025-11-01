package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusRequestGroupDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestGroupServiceDetail {

    // attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private StatusRequestGroupDetail status = StatusRequestGroupDetail.PENDING; // pending, approved, rejected
    private LocalDateTime solvedAt;

    // relationships
    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name = "staff_id")
    private Users staff;

    @OneToOne
    @JoinColumn(name = "request_group_id")
    @JsonIgnore
    private RequestGroupService requestGroupService;

}
