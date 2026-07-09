package com.example.rentrella.device.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_commands")
@Getter
@NoArgsConstructor
public class DeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    @Enumerated(EnumType.STRING)
    private CommandStatus status;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public DeviceCommand(Long deviceId, CommandStatus status) {
        this.deviceId = deviceId;
        this.status = status;
    }

    public void complete() {
        this.status = CommandStatus.DONE;
    }

    public void cancel() {
        this.status = CommandStatus.CANCELLED;
    }
}
