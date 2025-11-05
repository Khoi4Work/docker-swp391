package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundPaymentResponse {
    private String status;
    private String message;
    private String paymentUrl;
}
