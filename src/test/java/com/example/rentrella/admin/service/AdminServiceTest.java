package com.example.rentrella.admin.service;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.admin.dto.response.RentalLogResponse;
import com.example.rentrella.admin.entity.UserBan;
import com.example.rentrella.admin.repository.UserBanRepository;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.Device;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.device.repository.DeviceRepository;
import com.example.rentrella.umbrella.entity.RentalLog;
import com.example.rentrella.umbrella.entity.RentalStatus;
import com.example.rentrella.umbrella.repository.RentalLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    private RentalLogRepository rentalLogRepository;

    @Mock
    private UserBanRepository userBanRepository;

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
    void 우산_꽂이를_열때_기존에_대기중이던_명령이_있으면_취소한다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.of(newDevice()));

        DeviceCommand stalePending = new DeviceCommand(1L, CommandStatus.PENDING);
        given(deviceCommandRepository.findAllByDeviceIdAndStatus(1L, CommandStatus.PENDING))
                .willReturn(List.of(stalePending));

        adminService.openUmbrellaSlot(1L);

        assertThat(stalePending.getStatus()).isEqualTo(CommandStatus.CANCELLED);
        verify(deviceCommandRepository).saveAll(List.of(stalePending));
    }

    @Test
    void 존재하지_않는_우산_꽂이를_열면_예외를_던지고_명령을_생성하지_않는다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.openUmbrellaSlot(1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(deviceCommandRepository, never()).save(any());
    }

    @Test
    void 처음_금지되는_유저는_새로운_금지_기록을_생성한다() {
        given(userBanRepository.findByUserId(1L)).willReturn(Optional.empty());

        AdminActionResponse response = adminService.lockUser(1L);

        ArgumentCaptor<UserBan> banCaptor = ArgumentCaptor.forClass(UserBan.class);
        verify(userBanRepository).save(banCaptor.capture());
        assertThat(banCaptor.getValue().getUserId()).isEqualTo(1L);
        assertThat(banCaptor.getValue().getBannedUntil()).isAfter(LocalDateTime.now().plusDays(6));
        assertThat(response).isEqualTo(AdminActionResponse.of("대여 금지 처리 완료"));
    }

    @Test
    void 이미_금지된_유저는_금지_기간을_연장한다() {
        UserBan existing = new UserBan(1L, LocalDateTime.now().plusDays(1));
        given(userBanRepository.findByUserId(1L)).willReturn(Optional.of(existing));

        adminService.lockUser(1L);

        verify(userBanRepository).save(existing);
        assertThat(existing.getBannedUntil()).isAfter(LocalDateTime.now().plusDays(6));
    }

    @Test
    void 대여_반납_로그를_최신순으로_조회한다() {
        RentalLog log = new RentalLog(1L, 5L, RentalStatus.BORROW);
        given(rentalLogRepository.findAllByOrderByLogIdDesc()).willReturn(List.of(log));

        List<RentalLogResponse> responses = adminService.getRentalLogs();

        assertThat(responses).containsExactly(RentalLogResponse.of(log));
    }
}
