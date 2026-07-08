package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private AdminService adminService;

    private Device newDevice() {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "deviceId", 1L);
        return device;
    }

    @Test
    void 존재하는_우산_꽂이를_잠근다() {
        Device device = newDevice();
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        AdminActionResponse response = adminService.lockUmbrella(1L);

        assertThat(device.isLocked()).isTrue();
        verify(deviceRepository).save(device);
        assertThat(response).isEqualTo(AdminActionResponse.of("우산 꽂이 잠금 완료"));
    }

    @Test
    void 존재하지_않는_우산_꽂이를_잠그면_예외를_던진다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.lockUmbrella(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
