package com.example.rentrella.device.repository;

import com.example.rentrella.device.entity.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    private Device newDevice(boolean locked, boolean borrowed) {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "isLocked", locked);
        ReflectionTestUtils.setField(device, "isBorrowed", borrowed);
        return device;
    }

    @Test
    void 잠기지도_대여되지도_않은_디바이스_수를_센다() {
        deviceRepository.save(newDevice(false, false));
        deviceRepository.save(newDevice(false, false));
        deviceRepository.save(newDevice(false, true));
        deviceRepository.save(newDevice(true, false));

        assertThat(deviceRepository.countByIsLockedFalseAndIsBorrowedFalse()).isEqualTo(2L);
    }
}
