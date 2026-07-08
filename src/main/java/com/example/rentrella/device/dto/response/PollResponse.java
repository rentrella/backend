package com.example.rentrella.device.dto.response;

import com.example.rentrella.device.entity.DeviceCommand;

public record PollResponse(boolean hasCommand, Long commandId) {

    public static PollResponse none() {
        return new PollResponse(false, null);
    }

    public static PollResponse of(DeviceCommand command) {
        return new PollResponse(true, command.getId());
    }
}
