package com.example.rentrella.device.service;

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
import static org.mockito.BDDMockito.given;

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
}
