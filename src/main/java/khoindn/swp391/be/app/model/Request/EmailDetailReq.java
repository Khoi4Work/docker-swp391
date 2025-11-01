package khoindn.swp391.be.app.model.Request;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetailReq {

    private String email;
    private String subject;
    private String url;
    private String template;
}
