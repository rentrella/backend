package com.example.rentrella.auth.repository;

import com.example.rentrella.auth.domain.EmailVerificationCode;
import com.example.rentrella.auth.domain.EmailVerificationPurpose;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            EmailVerificationPurpose purpose
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailVerificationCode> findWithLockTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            EmailVerificationPurpose purpose
    );
}
