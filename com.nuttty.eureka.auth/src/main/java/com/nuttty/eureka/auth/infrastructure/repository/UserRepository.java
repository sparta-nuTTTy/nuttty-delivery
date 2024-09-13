package com.nuttty.eureka.auth.infrastructure.repository;

import com.nuttty.eureka.auth.domain.model.User;
import com.nuttty.eureka.auth.domain.repository.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
