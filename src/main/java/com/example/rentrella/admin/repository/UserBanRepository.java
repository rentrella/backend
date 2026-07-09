package com.example.rentrella.admin.repository;

import com.example.rentrella.admin.entity.UserBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBanRepository extends JpaRepository<UserBan, Long> {

    Optional<UserBan> findByUserId(Long userId);
}
