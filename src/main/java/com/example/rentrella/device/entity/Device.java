package com.example.rentrella.device.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "devices")
@Getter
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    private boolean isLocked;

    private boolean isBorrowed;

    public boolean isAvailable() {
        return !isLocked && !isBorrowed;
    }

    public void borrow() {
        this.isBorrowed = true;
    }

    public void returnRental() {
        this.isBorrowed = false;
    }
}
