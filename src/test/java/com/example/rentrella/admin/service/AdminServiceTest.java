package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceCommandRepository deviceCommandRepository;

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

    @Test
    void 존재하는_우산_꽂이를_열면_명령을_생성한다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.of(newDevice()));

        AdminActionResponse response = adminService.openUmbrellaSlot(1L);

        ArgumentCaptor<DeviceCommand> commandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandRepository).save(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getDeviceId()).isEqualTo(1L);
        assertThat(commandCaptor.getValue().getStatus()).isEqualTo(CommandStatus.PENDING);
        assertThat(response).isEqualTo(AdminActionResponse.of("명령 접수됨"));
    }

    @Test
    void 존재하지_않는_우산_꽂이를_열면_예외를_던지고_명령을_생성하지_않는다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.openUmbrellaSlot(1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(deviceCommandRepository, never()).save(any());
    }
}
