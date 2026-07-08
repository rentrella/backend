package com.example.rentrella.device.service;

import com.example.rentrella.device.dto.response.CompleteCommandResponse;
import com.example.rentrella.device.dto.response.PollResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceCommandRepository deviceCommandRepository;

    public PollResponse pollLatestCommand(Long deviceId) {
        return deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(deviceId, CommandStatus.PENDING)
                .map(PollResponse::of)
                .orElseGet(PollResponse::none);
    }

    @Transactional
    public CompleteCommandResponse completeCommand(Long commandId) {
        DeviceCommand command = deviceCommandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 명령입니다. commandId=" + commandId));

        if (command.getStatus() != CommandStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 명령입니다. status=" + command.getStatus());
        }

        command.complete();
        deviceCommandRepository.save(command);

        return CompleteCommandResponse.success();
    }
}
