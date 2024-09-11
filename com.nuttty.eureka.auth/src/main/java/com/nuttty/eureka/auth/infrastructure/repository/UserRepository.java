package com.nuttty.eureka.auth.infrastructure.repository;

import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.repository.JpaUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaUserRepository {

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
