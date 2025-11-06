package khoindn.swp391.be.app.model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {
    @Positive
    private Integer groupId;
    @NotBlank
    @Size(min = 1, max = 50, message = "Request name name must be between 1 and 50 characters")
    private String nameRequestGroup;
    private String descriptionRequestGroup = "No description";

}
