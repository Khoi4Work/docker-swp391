package khoindn.swp391.be.app.model.Request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestGroup {
    @Positive
    Long idRequestGroup;
    @Range(min = 0, max = 1)
    int idChoice;
}
