package com.nuttty.eureka.auth.domain.model;

import com.nuttty.eureka.auth.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_people")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(value = AuditingEntityListener.class)
public class DeliveryPerson extends AuditEntity {

    @Id
    @Column(nullable = false)
    private Long deliveryPersonId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private UUID hubId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DeliveryPersonTypeEnum deliveryPersonType;

    @Column(nullable = false, length = 100)
    private String slackId;


    // 배송 담당자 등록
    public static DeliveryPerson create(Long userId, UUID hubId, DeliveryPersonTypeEnum deliveryPersonType, String slackId) {
        return DeliveryPerson.builder()
                .deliveryPersonId(userId)
                .userId(userId)
                .hubId(hubId)
                .deliveryPersonType(deliveryPersonType)
                .slackId(slackId)
                .build();
    }
}