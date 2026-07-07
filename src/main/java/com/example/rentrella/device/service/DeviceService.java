package com.example.rentrella.device.service;

import com.example.rentrella.device.dto.PollResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceCommandRepository deviceCommandRepository;

    public PollResponse pollLatestCommand(Long deviceId) {
        return deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(deviceId, CommandStatus.PENDING)
                .map(PollResponse::of)
                .orElseGet(PollResponse::none);
    }
}
