package khoindn.swp391.be.app.model.Request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    private String contract_signature;

    @NotNull
    private MultipartFile contractContent;

}
