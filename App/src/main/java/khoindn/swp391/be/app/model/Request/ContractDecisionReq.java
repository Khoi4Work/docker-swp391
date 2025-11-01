package khoindn.swp391.be.app.model.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDecisionReq {

    @Positive(message = "Contract ID must be greater than 0")
    private int idContract;

    @Positive(message = "User ID must be greater than 0")
    private int idUser;

    @Min(value = 0, message = "Choice must be 0 (Declined) or 1 (Signed)")
    @Max(value = 1, message = "Choice must be 0 (Declined) or 1 (Signed)")
    private int idChoice;

    @NotBlank
    private String contractContent;

    @NotBlank
    private String contract_signature;

}
