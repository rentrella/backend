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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UmbrellaServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceCommandRepository deviceCommandRepository;

    @Mock
    private RentalLogRepository rentalLogRepository;

    @InjectMocks
    private UmbrellaService umbrellaService;

    private Device newDevice(boolean locked, boolean borrowed) {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "deviceId", 1L);
        ReflectionTestUtils.setField(device, "isLocked", locked);
        ReflectionTestUtils.setField(device, "isBorrowed", borrowed);
        return device;
    }

    @Test
    void 대여_가능한_우산_수를_반환한다() {
        given(deviceRepository.countByIsLockedFalseAndIsBorrowedFalse()).willReturn(3L);

        AvailableCountResponse response = umbrellaService.getAvailableCount();

        assertThat(response).isEqualTo(new AvailableCountResponse(3L));
    }

    @Test
    void 대여_가능한_우산이면_is_borrowed를_true로_바꾸고_명령과_로그를_생성한다() {
        Device device = newDevice(false, false);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        RentResponse response = umbrellaService.rentUmbrella(10L, 1L);

        assertThat(device.isBorrowed()).isTrue();
        assertThat(response).isEqualTo(RentResponse.accepted());
        verify(deviceRepository).save(device);

        ArgumentCaptor<DeviceCommand> commandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandRepository).save(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getDeviceId()).isEqualTo(1L);
        assertThat(commandCaptor.getValue().getStatus()).isEqualTo(CommandStatus.PENDING);

        ArgumentCaptor<RentalLog> logCaptor = ArgumentCaptor.forClass(RentalLog.class);
        verify(rentalLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getUserId()).isEqualTo(10L);
        assertThat(logCaptor.getValue().getDeviceId()).isEqualTo(1L);
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(RentalStatus.BORROW);
    }

    @Test
    void 존재하지_않는_deviceId면_예외를_던진다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> umbrellaService.rentUmbrella(10L, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(deviceCommandRepository, never()).save(any());
        verify(rentalLogRepository, never()).save(any());
    }

    @Test
    void 이미_대여중이면_예외를_던지고_아무것도_저장하지_않는다() {
        Device device = newDevice(false, true);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        assertThatThrownBy(() -> umbrellaService.rentUmbrella(10L, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(deviceRepository, never()).save(any());
        verify(deviceCommandRepository, never()).save(any());
        verify(rentalLogRepository, never()).save(any());
    }

    @Test
    void 잠긴_우산이면_대여시_예외를_던진다() {
        Device device = newDevice(true, false);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        assertThatThrownBy(() -> umbrellaService.rentUmbrella(10L, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(deviceRepository, never()).save(any());
    }

    @Test
    void 대여중인_우산이면_반납시_is_borrowed를_false로_바꾸고_명령과_로그를_생성한다() {
        Device device = newDevice(false, true);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        RentResponse response = umbrellaService.returnUmbrella(10L, 1L);

        assertThat(device.isBorrowed()).isFalse();
        assertThat(response).isEqualTo(RentResponse.accepted());
        verify(deviceRepository).save(device);

        ArgumentCaptor<DeviceCommand> commandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandRepository).save(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getStatus()).isEqualTo(CommandStatus.PENDING);

        ArgumentCaptor<RentalLog> logCaptor = ArgumentCaptor.forClass(RentalLog.class);
        verify(rentalLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getUserId()).isEqualTo(10L);
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(RentalStatus.RETURN);
    }

    @Test
    void 대여시_기존에_대기중이던_명령이_있으면_취소한다() {
        Device device = newDevice(false, false);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        DeviceCommand stalePending = new DeviceCommand(1L, CommandStatus.PENDING);
        given(deviceCommandRepository.findAllByDeviceIdAndStatus(1L, CommandStatus.PENDING))
                .willReturn(List.of(stalePending));

        umbrellaService.rentUmbrella(10L, 1L);

        assertThat(stalePending.getStatus()).isEqualTo(CommandStatus.CANCELLED);
        verify(deviceCommandRepository).saveAll(List.of(stalePending));
    }

    @Test
    void 반납_대상_deviceId가_존재하지_않으면_예외를_던진다() {
        given(deviceRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> umbrellaService.returnUmbrella(10L, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(deviceCommandRepository, never()).save(any());
        verify(rentalLogRepository, never()).save(any());
    }

    @Test
    void 대여중이_아니면_반납시_예외를_던지고_아무것도_저장하지_않는다() {
        Device device = newDevice(false, false);
        given(deviceRepository.findById(1L)).willReturn(Optional.of(device));

        assertThatThrownBy(() -> umbrellaService.returnUmbrella(10L, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(deviceRepository, never()).save(any());
        verify(deviceCommandRepository, never()).save(any());
        verify(rentalLogRepository, never()).save(any());
    }

    @Test
    void 최근_로그가_BORROW이면_대여중인_deviceId를_반환한다() {
        RentalLog log = new RentalLog(10L, 5L, RentalStatus.BORROW);
        given(rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(10L)).willReturn(Optional.of(log));

        MyRentalResponse response = umbrellaService.getMyRentedUmbrella(10L);

        assertThat(response).isEqualTo(MyRentalResponse.of(5L));
    }

    @Test
    void 최근_로그가_RETURN이면_대여중이_아니다() {
        RentalLog log = new RentalLog(10L, 5L, RentalStatus.RETURN);
        given(rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(10L)).willReturn(Optional.of(log));

        MyRentalResponse response = umbrellaService.getMyRentedUmbrella(10L);

        assertThat(response).isEqualTo(MyRentalResponse.none());
    }

    @Test
    void 로그가_없으면_대여중이_아니다() {
        given(rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(10L)).willReturn(Optional.empty());

        MyRentalResponse response = umbrellaService.getMyRentedUmbrella(10L);

        assertThat(response).isEqualTo(MyRentalResponse.none());
    }
}
