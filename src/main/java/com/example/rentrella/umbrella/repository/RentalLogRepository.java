package com.example.rentrella.umbrella.repository;

import com.example.rentrella.umbrella.entity.RentalLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalLogRepository extends JpaRepository<RentalLog, Long> {

    Optional<RentalLog> findFirstByUserIdOrderByLogIdDesc(Long userId);

    List<RentalLog> findAllByOrderByLogIdDesc();
}
