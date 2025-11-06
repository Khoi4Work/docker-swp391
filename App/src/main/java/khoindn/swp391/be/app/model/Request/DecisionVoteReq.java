package khoindn.swp391.be.app.model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionVoteReq {

    @NotBlank
    @Size(min = 1, max = 50)
    private String decisionName; // maintenance, repair, upgrade,... or others

    @Size(min = 1, max = 50)
    private String description;

}
