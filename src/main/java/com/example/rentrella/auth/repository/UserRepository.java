package com.example.rentrella.auth.repository;

import com.example.rentrella.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByDataGsmId(String dataGsmId);

    boolean existsByEmail(String email);
}
