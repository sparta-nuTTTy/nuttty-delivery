package com.nuttty.eureka.auth.domain.repository;

import com.nuttty.eureka.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);
}
