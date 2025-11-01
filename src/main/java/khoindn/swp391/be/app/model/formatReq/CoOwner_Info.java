package khoindn.swp391.be.app.model.formatReq;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoOwner_Info {

    private int coOwnerId;
    @NotNull(message = "Ownership percentage cannot be null")
    @DecimalMin(value = "14.9", message = "Ownership percentage must be greater than 14.9")
    @DecimalMax(value = "100.0", message = "Ownership percentage must not exceed 100")
    private Float ownershipPercentage;
    @NotNull(message = "Role in group cannot be null")
    private String roleInGroup;
}
