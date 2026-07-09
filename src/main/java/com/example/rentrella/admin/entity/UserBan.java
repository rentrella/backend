package com.example.rentrella.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_bans")
@Getter
@NoArgsConstructor
public class UserBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime bannedUntil;

    public UserBan(Long userId, LocalDateTime bannedUntil) {
        this.userId = userId;
        this.bannedUntil = bannedUntil;
    }

    public void extend(LocalDateTime bannedUntil) {
        this.bannedUntil = bannedUntil;
    }
}
