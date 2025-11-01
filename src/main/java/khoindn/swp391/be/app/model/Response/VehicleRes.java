package khoindn.swp391.be.app.model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRes {
    private int vehicleId;
    private String plateNo;
    private String brand;
    private String model;
    private String color;
    private int batteryCapacity;
    private LocalDateTime createdAt;
    private int price;
    private String imageUrl;

    // Thông tin group
    private int groupId;
    private String groupName;
}
