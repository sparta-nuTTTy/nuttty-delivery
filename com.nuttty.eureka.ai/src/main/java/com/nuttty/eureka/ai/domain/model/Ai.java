package com.nuttty.eureka.ai.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_ai")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_delete = false")
@Getter
public class Ai extends AuditEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "ai_id")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "delivery_manager_id")
    private UUID deliveryManagerId;

    public Ai(String answer, UUID deliveryManagerId) {
        this.question = question;
        this.answer = answer;
        this.deliveryManagerId = deliveryManagerId;
    }

    public static Ai createAi(String answer, UUID deliveryManagerId) {
        return new Ai(answer, deliveryManagerId);
    }
}
