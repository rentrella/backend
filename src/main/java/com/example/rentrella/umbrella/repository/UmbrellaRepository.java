package com.example.rentrella.umbrella.repository;

import com.example.rentrella.umbrella.entity.Umbrella;
import com.example.rentrella.umbrella.entity.UmbrellaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {

    Optional<Umbrella> findByDeviceId(Long deviceId);

    long countByStatus(UmbrellaStatus status);
}
