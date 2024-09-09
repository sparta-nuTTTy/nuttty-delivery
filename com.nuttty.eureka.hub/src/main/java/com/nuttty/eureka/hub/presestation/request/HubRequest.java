package com.nuttty.eureka.hub.presestation.request;

import com.nuttty.eureka.hub.domain.model.Hub;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubRequest {

    @NotNull(message = "User ID must be provided")
    private Long user_id;

    @NotNull(message = "Name must be provided.")
    @NotBlank(message = "Name must be provided.")
    private String name;

    @NotNull(message = "Address must be provided")
    @NotBlank(message = "Address must be provided")
    private String address;

    @NotNull(message = "Latitude must be provided")
    @NotBlank(message = "Latitude must be provided")
    private String latitude;

    @NotNull(message = "Longitude must be provided")
    @NotBlank(message = "Longitude must be provided")
    private String longitude;

    public Hub toEntity() {
        return new Hub(name, address, latitude, longitude, user_id);
    }
}
