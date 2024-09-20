package com.nuttty.eureka.hub.presestation.request;

import com.nuttty.eureka.hub.domain.model.Hub;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubRequestDto {

    @NotNull(message = "User ID must be provided")
    @Schema(description = "허브 관리자 ID", example = "1", type = "integer", format = "int64")
    private Long user_id;

    @NotNull(message = "Name must be provided.")
    @NotBlank(message = "Name must be provided.")
    @Schema(description = "허브 이름", example = "서울특별시 센터", type = "string")
    private String name;

    @NotNull(message = "Address must be provided")
    @NotBlank(message = "Address must be provided")
    @Schema(description = "허브 주소", example = "서울특별시 송파구 송파대로 55", type = "string")
    private String address;

    @NotNull(message = "Latitude must be provided")
    @NotBlank(message = "Latitude must be provided")
    @Schema(description = "허브 위도", example = "37.5111", type = "string")
    private String latitude;

    @NotNull(message = "Longitude must be provided")
    @NotBlank(message = "Longitude must be provided")
    @Schema(description = "허브 경도", example = "127.1048", type = "string")
    private String longitude;

    @NotNull(message = "nx must be provided")
    @NotBlank(message = "nx must be provided")
    @Schema(description = "격자 좌표", example = "61", type = "string")
    private String nx;

    @NotNull(message = "nx must be provided")
    @NotBlank(message = "nx must be provided")
    @Schema(description = "격자 좌표", example = "126", type = "string")
    private String ny;

    public Hub toEntity() {
        return new Hub(name, address, latitude, longitude, user_id, nx, ny);
    }
}
