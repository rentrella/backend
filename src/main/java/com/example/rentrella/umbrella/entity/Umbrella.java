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
@Table(name = "umbrella")
@Getter
@NoArgsConstructor
public class Umbrella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    @Enumerated(EnumType.STRING)
    private UmbrellaStatus status;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Umbrella(Long deviceId, UmbrellaStatus status) {
        this.deviceId = deviceId;
        this.status = status;
    }

    public void rent() {
        this.status = UmbrellaStatus.RENTED;
    }

    public void returnRental() {
        this.status = UmbrellaStatus.AVAILABLE;
    }
}
