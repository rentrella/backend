package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public AdminActionResponse lockUmbrella(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        device.lock();
        deviceRepository.save(device);

        return AdminActionResponse.of("우산 꽂이 잠금 완료");
    }
}
