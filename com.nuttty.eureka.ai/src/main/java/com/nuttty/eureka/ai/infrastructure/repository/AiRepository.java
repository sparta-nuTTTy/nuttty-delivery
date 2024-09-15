package com.nuttty.eureka.ai.infrastructure.repository;

import com.nuttty.eureka.ai.domain.model.Ai;
import com.nuttty.eureka.ai.domain.repository.AiRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID>, AiRepositoryCustom {
}
