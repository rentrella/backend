package com.example.rentrella.device.repository;

import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeviceCommandRepositoryTest {

    @Autowired
    private DeviceCommandRepository deviceCommandRepository;

    @Test
    void 가장_먼저_생성된_PENDING_명령을_조회한다() {
        deviceCommandRepository.save(new DeviceCommand(1L, CommandStatus.DONE));
        DeviceCommand oldest = deviceCommandRepository.save(new DeviceCommand(1L, CommandStatus.PENDING));
        deviceCommandRepository.save(new DeviceCommand(1L, CommandStatus.PENDING));

        assertThat(deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(1L, CommandStatus.PENDING))
                .isPresent()
                .get()
                .extracting(DeviceCommand::getId)
                .isEqualTo(oldest.getId());
    }

    @Test
    void PENDING_명령이_없으면_빈_값을_반환한다() {
        deviceCommandRepository.save(new DeviceCommand(2L, CommandStatus.DONE));

        assertThat(deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(2L, CommandStatus.PENDING))
                .isEmpty();
    }
}
