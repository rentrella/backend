package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DeviceRepository deviceRepository;
    private final DeviceCommandRepository deviceCommandRepository;

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

        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));

        return AdminActionResponse.of("명령 접수됨");
    }
}
