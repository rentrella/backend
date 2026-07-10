package com.example.rentrella.umbrella.entity;

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
@Table(name = "rental_logs")
@Getter
@NoArgsConstructor
public class RentalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private Long userId;

    private Long deviceId;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public RentalLog(Long userId, Long deviceId, RentalStatus status) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.status = status;
    }
}
