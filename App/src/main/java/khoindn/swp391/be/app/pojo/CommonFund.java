package khoindn.swp391.be.app.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commonfund")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonFund {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fund_id")
    private Integer fundId;
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;

    // Relationships
    @OneToOne
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    private Group group;
    @OneToMany(mappedBy = "commonFund", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FundDetail> fundDetails = new ArrayList<>();

}
