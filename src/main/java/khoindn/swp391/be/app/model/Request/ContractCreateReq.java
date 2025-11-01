package khoindn.swp391.be.app.model.Request;

import jakarta.mail.Multipart;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractCreateReq {


    @NotBlank(message = "Can't get url!!")
    private String documentUrl;
    @NotBlank(message = "Can't get contractType!!")
    private String contractType;
    @NotEmpty(message = "UserId list cannot be empty")
    public List<Integer> userId;

    @NotBlank
    private MultipartFile imageContract;

    @NotBlank
    private String plateNo;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String color;

    @NotNull
    private int batteryCapacity;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Price must be a valid number with up to 2 decimal places")
    private float price;

    private MultipartFile vehicleImage;

}
