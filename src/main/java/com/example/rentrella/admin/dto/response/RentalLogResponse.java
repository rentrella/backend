package com.example.rentrella.admin.dto.response;

import com.example.rentrella.umbrella.entity.RentalLog;

import java.time.LocalDateTime;

public record RentalLogResponse(Long logId, Long userId, Long deviceId, String status, LocalDateTime createdAt) {

    public static RentalLogResponse of(RentalLog log) {
        return new RentalLogResponse(log.getLogId(), log.getUserId(), log.getDeviceId(),
                log.getStatus().name(), log.getCreatedAt());
    }
}
