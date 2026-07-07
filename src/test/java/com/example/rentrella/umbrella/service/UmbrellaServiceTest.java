package com.example.rentrella.umbrella.service;

import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
import com.example.rentrella.umbrella.dto.RentResponse;
import com.example.rentrella.umbrella.entity.Umbrella;
import com.example.rentrella.umbrella.entity.UmbrellaStatus;
import com.example.rentrella.umbrella.repository.UmbrellaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private UmbrellaRepository umbrellaRepository;

    @Mock
    private DeviceCommandRepository deviceCommandRepository;

    @InjectMocks
    private UmbrellaService umbrellaService;

    @Test
    void 대여_가능한_우산이면_상태를_RENTED로_바꾸고_대기_명령을_생성한다() {
        Umbrella umbrella = new Umbrella(1L, UmbrellaStatus.AVAILABLE);
        given(umbrellaRepository.findByDeviceId(1L)).willReturn(Optional.of(umbrella));

        RentResponse response = umbrellaService.rentUmbrella(1L);

        assertThat(umbrella.getStatus()).isEqualTo(UmbrellaStatus.RENTED);
        assertThat(response).isEqualTo(RentResponse.accepted());
        verify(umbrellaRepository).save(umbrella);

        ArgumentCaptor<DeviceCommand> captor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandRepository).save(captor.capture());
        assertThat(captor.getValue().getDeviceId()).isEqualTo(1L);
        assertThat(captor.getValue().getStatus()).isEqualTo(CommandStatus.PENDING);
    }

    @Test
    void 존재하지_않는_deviceId면_예외를_던진다() {
        given(umbrellaRepository.findByDeviceId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> umbrellaService.rentUmbrella(1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(deviceCommandRepository, never()).save(any());
    }

    @Test
    void 이미_대여중이면_예외를_던지고_아무것도_저장하지_않는다() {
        Umbrella umbrella = new Umbrella(1L, UmbrellaStatus.RENTED);
        given(umbrellaRepository.findByDeviceId(1L)).willReturn(Optional.of(umbrella));

        assertThatThrownBy(() -> umbrellaService.rentUmbrella(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(umbrellaRepository, never()).save(any());
        verify(deviceCommandRepository, never()).save(any());
    }
}
