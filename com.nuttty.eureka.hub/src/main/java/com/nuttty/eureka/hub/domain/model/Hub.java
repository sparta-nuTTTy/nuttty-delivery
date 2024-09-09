package com.nuttty.eureka.hub.domain.model;

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

    @Column(unique = true)
    private String name;

    @Column(unique = true)
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
}
