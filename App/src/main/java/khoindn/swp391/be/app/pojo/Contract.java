package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.StatusContract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractId;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private StatusContract status = StatusContract.PENDING_REVIEW;
    @Column(name = "content_string", length = 5000)
    private String htmlString; // link PDF do FE render
    @Column(name = "url_contract")
    private String urlConfirmedContract;
    @Column(name = "image_contract")
    private String imageContract;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Users staff;

    @OneToMany(mappedBy = "contract")
    @JsonIgnore
    private List<ContractSigner> signerList;
}
