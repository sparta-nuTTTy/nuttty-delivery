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

    private String name;
    private String address;
    private String latitude;
    private String longitude;

    private Long user_id;

    public Hub(String name, String address, String latitude, String longitude, Long user_id) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
    }
}
