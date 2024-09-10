package com.nuttty.eureka.hub.domain.model;

import com.nuttty.eureka.hub.presestation.request.HubRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_hubs")
public class Hub extends AuditEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "hub_id")
    private UUID id;

//    @Column(unique = true)
    private String name;

//    @Column(unique = true)
    private String address;
    private String latitude;
    private String longitude;

    @Column(name = "user_id")
    private Long userId;

    public Hub(String name, String address, String latitude, String longitude, Long userId) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    /**
     * 허브 수정 메서드
     * @param request
     * @return
     */
    public Hub update(HubRequestDto request) {
        this.name = request.getName();
        this.address = request.getAddress();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.userId = request.getUser_id();
        return this;
    }
}
