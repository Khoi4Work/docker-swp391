package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractHistoryRes {
    private int contractId;
    private String vehicleName;
    private float ownership;
    private String status;
    private LocalDate signedAt;
}
