package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.admin.dto.response.RentalLogResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import com.example.rentrella.umbrella.repository.RentalLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DeviceRepository deviceRepository;
    private final DeviceCommandRepository deviceCommandRepository;
    private final RentalLogRepository rentalLogRepository;

    @Transactional
    public AdminActionResponse lockUmbrella(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        device.lock();
        deviceRepository.save(device);

        return AdminActionResponse.of("우산 꽂이 잠금 완료");
    }

    @Transactional
    public AdminActionResponse openUmbrellaSlot(Long deviceId) {
        deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        List<DeviceCommand> pendingCommands = deviceCommandRepository.findAllByDeviceIdAndStatus(deviceId, CommandStatus.PENDING);
        pendingCommands.forEach(DeviceCommand::cancel);
        deviceCommandRepository.saveAll(pendingCommands);

        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));

        return AdminActionResponse.of("명령 접수됨");
    }

    // TODO: feat/auth의 User 엔티티(users.end_banned) 병합 후 구현 — 7일 뒤 만료되는 대여 금지 처리
    public AdminActionResponse lockUser(Long userId) {
        throw new UnsupportedOperationException();
    }

    public List<RentalLogResponse> getRentalLogs() {
        return rentalLogRepository.findAllByOrderByLogIdDesc().stream()
                .map(RentalLogResponse::of)
                .toList();
    }
}
