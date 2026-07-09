package com.example.rentrella.device.repository;

import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {

    Optional<DeviceCommand> findFirstByDeviceIdAndStatusOrderByIdAsc(Long deviceId, CommandStatus status);

    List<DeviceCommand> findAllByDeviceIdAndStatus(Long deviceId, CommandStatus status);
}
