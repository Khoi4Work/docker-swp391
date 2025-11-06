package khoindn.swp391.be.app.pojo;

import jakarta.persistence.*;
import khoindn.swp391.be.app.pojo._enum.DecisionContractSigner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_signers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractSigner {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "decision")
    @Enumerated(EnumType.STRING)
    private DecisionContractSigner decision = DecisionContractSigner.PENDING; // pending | signed | declined

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "signature", length = 3000)
    private String signature;

    // Relationship
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
