package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverrideInfoRes {
    private int userId;
    private int groupId;
    private long overridesUsed;
    private long overridesRemaining;
    private int maxOverridesPerMonth;
    private String currentMonth;
    private LocalDate nextResetDate;

}
