package com.example.rentrella.umbrella.service;

import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import com.example.rentrella.umbrella.dto.response.AvailableCountResponse;
import com.example.rentrella.umbrella.dto.response.MyRentalResponse;
import com.example.rentrella.umbrella.dto.response.RentResponse;
import com.example.rentrella.umbrella.entity.RentalLog;
import com.example.rentrella.umbrella.entity.RentalStatus;
import com.example.rentrella.umbrella.repository.RentalLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UmbrellaService {

    private final DeviceRepository deviceRepository;
    private final DeviceCommandRepository deviceCommandRepository;
    private final RentalLogRepository rentalLogRepository;

    public AvailableCountResponse getAvailableCount() {
        return new AvailableCountResponse(deviceRepository.countByIsLockedFalseAndIsBorrowedFalse());
    }

    @Transactional
    public RentResponse rentUmbrella(Long userId, Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        if (!device.isAvailable()) {
            throw new IllegalStateException("대여할 수 없는 상태입니다. deviceId=" + deviceId);
        }

        device.borrow();
        deviceRepository.save(device);
        cancelPendingCommands(deviceId);
        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));
        rentalLogRepository.save(new RentalLog(userId, deviceId, RentalStatus.BORROW));

        return RentResponse.accepted();
    }

    @Transactional
    public RentResponse returnUmbrella(Long userId, Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 우산 꽂이입니다. deviceId=" + deviceId));

        if (!device.isBorrowed()) {
            throw new IllegalStateException("반납할 수 없는 상태입니다. deviceId=" + deviceId);
        }

        device.returnRental();
        deviceRepository.save(device);
        cancelPendingCommands(deviceId);
        deviceCommandRepository.save(new DeviceCommand(deviceId, CommandStatus.PENDING));
        rentalLogRepository.save(new RentalLog(userId, deviceId, RentalStatus.RETURN));

        return RentResponse.accepted();
    }

    public MyRentalResponse getMyRentedUmbrella(Long userId) {
        return rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(userId)
                .filter(log -> log.getStatus() == RentalStatus.BORROW)
                .map(log -> MyRentalResponse.of(log.getDeviceId()))
                .orElseGet(MyRentalResponse::none);
    }

    private void cancelPendingCommands(Long deviceId) {
        List<DeviceCommand> pendingCommands = deviceCommandRepository.findAllByDeviceIdAndStatus(deviceId, CommandStatus.PENDING);
        pendingCommands.forEach(DeviceCommand::cancel);
        deviceCommandRepository.saveAll(pendingCommands);
    }
}
