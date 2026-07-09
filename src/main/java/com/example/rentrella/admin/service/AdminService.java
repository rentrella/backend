package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.admin.dto.response.RentalLogResponse;
import com.example.rentrella.admin.entity.UserBan;
import com.example.rentrella.admin.repository.UserBanRepository;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import com.example.rentrella.umbrella.repository.RentalLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final long USER_BAN_DAYS = 7;

    private final DeviceRepository deviceRepository;
    private final DeviceCommandRepository deviceCommandRepository;
    private final RentalLogRepository rentalLogRepository;
    private final UserBanRepository userBanRepository;

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

    @Transactional
    public AdminActionResponse lockUser(Long userId) {
        LocalDateTime bannedUntil = LocalDateTime.now().plusDays(USER_BAN_DAYS);

        UserBan userBan = userBanRepository.findByUserId(userId)
                .orElseGet(() -> new UserBan(userId, bannedUntil));
        userBan.extend(bannedUntil);
        userBanRepository.save(userBan);

        return AdminActionResponse.of("대여 금지 처리 완료");
    }

    public List<RentalLogResponse> getRentalLogs() {
        return rentalLogRepository.findAllByOrderByLogIdDesc().stream()
                .map(RentalLogResponse::of)
                .toList();
    }
}
