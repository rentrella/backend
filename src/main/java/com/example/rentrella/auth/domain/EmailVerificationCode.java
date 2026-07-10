package com.example.rentrella.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 64)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmailVerificationPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private int failedAttempts;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private EmailVerificationCode(String email, String codeHash, EmailVerificationPurpose purpose, LocalDateTime expiresAt) {
        this.email = email;
        this.codeHash = codeHash;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.used = false;
        this.failedAttempts = 0;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isValid(String codeHash, int maxFailedAttempts) {
        return !used
                && failedAttempts < maxFailedAttempts
                && this.codeHash.equals(codeHash)
                && expiresAt.isAfter(LocalDateTime.now());
    }

    public void use() {
        this.used = true;
    }

    public void fail() {
        this.failedAttempts++;
    }
}
