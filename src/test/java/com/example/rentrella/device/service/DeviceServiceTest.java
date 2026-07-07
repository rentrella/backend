package com.example.rentrella.device.service;

import com.example.rentrella.device.dto.CompleteCommandResponse;
import com.example.rentrella.device.dto.PollResponse;
import com.example.rentrella.device.entity.CommandStatus;
import com.example.rentrella.device.entity.DeviceCommand;
import com.example.rentrella.device.repository.DeviceCommandRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceCommandRepository deviceCommandRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void 대기중인_명령이_있으면_hasCommand_true와_명령ID를_반환한다() {
        DeviceCommand command = new DeviceCommand(1L, CommandStatus.PENDING);
        ReflectionTestUtils.setField(command, "id", 102L);
        given(deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(1L, CommandStatus.PENDING))
                .willReturn(Optional.of(command));

        PollResponse response = deviceService.pollLatestCommand(1L);

        assertThat(response).isEqualTo(new PollResponse(true, 102L));
    }

    @Test
    void 대기중인_명령이_없으면_hasCommand_false를_반환한다() {
        given(deviceCommandRepository.findFirstByDeviceIdAndStatusOrderByIdAsc(1L, CommandStatus.PENDING))
                .willReturn(Optional.empty());

        PollResponse response = deviceService.pollLatestCommand(1L);

        assertThat(response).isEqualTo(PollResponse.none());
    }

    @Test
    void 대기중인_명령을_완료처리하면_상태가_DONE으로_바뀐다() {
        DeviceCommand command = new DeviceCommand(1L, CommandStatus.PENDING);
        given(deviceCommandRepository.findById(102L)).willReturn(Optional.of(command));

        CompleteCommandResponse response = deviceService.completeCommand(102L);

        assertThat(command.getStatus()).isEqualTo(CommandStatus.DONE);
        assertThat(response).isEqualTo(CompleteCommandResponse.success());
        verify(deviceCommandRepository).save(command);
    }

    @Test
    void 존재하지_않는_명령이면_예외를_던진다() {
        given(deviceCommandRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.completeCommand(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이미_완료된_명령이면_예외를_던지고_저장하지_않는다() {
        DeviceCommand command = new DeviceCommand(1L, CommandStatus.DONE);
        given(deviceCommandRepository.findById(102L)).willReturn(Optional.of(command));

        assertThatThrownBy(() -> deviceService.completeCommand(102L))
                .isInstanceOf(IllegalStateException.class);

        verify(deviceCommandRepository, never()).save(command);
    }
}
