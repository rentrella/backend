package com.example.rentrella.umbrella.service;

import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.umbrella.dto.AvailableCountResponse;
import com.example.rentrella.umbrella.dto.RentResponse;
import com.example.rentrella.umbrella.entity.Umbrella;
import com.example.rentrella.umbrella.entity.UmbrellaStatus;
import com.example.rentrella.umbrella.repository.UmbrellaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UmbrellaService {

    private final UmbrellaRepository umbrellaRepository;
    private final DeviceCommandRepository deviceCommandRepository;

    public AvailableCountResponse getAvailableCount() {
        return new AvailableCountResponse(umbrellaRepository.countByStatus(UmbrellaStatus.AVAILABLE));
    }

    @Transactional
    public RentResponse rentUmbrella(Long deviceId) {
        Umbrella umbrella = umbrellaRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        if (umbrella.getStatus() != UmbrellaStatus.AVAILABLE) {
            throw new IllegalStateException("대여할 수 없는 상태입니다. status=" + umbrella.getStatus());
        }

        umbrella.rent();
        umbrellaRepository.save(umbrella);
        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));

        return RentResponse.accepted();
    }

    @Transactional
    public RentResponse returnUmbrella(Long deviceId) {
        Umbrella umbrella = umbrellaRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        if (umbrella.getStatus() != UmbrellaStatus.RENTED) {
            throw new IllegalStateException("반납할 수 없는 상태입니다. status=" + umbrella.getStatus());
        }

        umbrella.returnRental();
        umbrellaRepository.save(umbrella);
        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));

        return RentResponse.accepted();
    }
}
