package com.example.rentrella.auth.service;

import com.example.rentrella.auth.domain.EmailVerificationCode;
import com.example.rentrella.auth.domain.EmailVerificationPurpose;
import com.example.rentrella.auth.domain.RefreshToken;
import com.example.rentrella.auth.domain.User;
import com.example.rentrella.auth.domain.UserRole;
import com.example.rentrella.auth.dto.EmailCodeRequest;
import com.example.rentrella.auth.dto.LoginRequest;
import com.example.rentrella.auth.dto.LogoutRequest;
import com.example.rentrella.auth.dto.PasswordResetRequest;
import com.example.rentrella.auth.dto.ReissueRequest;
import com.example.rentrella.auth.dto.SignupRequest;
import com.example.rentrella.auth.dto.SignupResponse;
import com.example.rentrella.auth.dto.TokenResponse;
import com.example.rentrella.auth.exception.AuthException;
import com.example.rentrella.auth.repository.EmailVerificationCodeRepository;
import com.example.rentrella.auth.repository.RefreshTokenRepository;
import com.example.rentrella.auth.repository.UserRepository;
import com.example.rentrella.auth.security.JwtTokenProvider;
import com.example.rentrella.auth.security.Sha256Hash;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailCodeSender emailCodeSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.auth.email-code-validity-minutes}")
    private long emailCodeValidityMinutes;

    @Value("${app.auth.email-code-max-failed-attempts}")
    private int emailCodeMaxFailedAttempts;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        if (user.isBanned()) {
            throw new AuthException(HttpStatus.FORBIDDEN, "This account is banned.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        return issueTokens(user);
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(HttpStatus.CONFLICT, "Email already exists.");
        }

        verifyEmailCode(request.email(), request.verificationCode(), EmailVerificationPurpose.SIGNUP);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .studentNumber(request.studentNumber())
                .dataGsmId(request.dataGsmId())
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        return new SignupResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getName());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByTokenHash(Sha256Hash.value(request.refreshToken()))
                .ifPresent(RefreshToken::revoke);
    }

    @Transactional
    public TokenResponse reissue(ReissueRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findWithLockByTokenHash(Sha256Hash.value(request.refreshToken()))
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token not found."));

        if (!refreshToken.isUsable()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked.");
        }

        try {
            if (!jwtTokenProvider.isRefreshToken(request.refreshToken())) {
                throw new AuthException(HttpStatus.UNAUTHORIZED, "Token is not a refresh token.");
            }
        } catch (JwtException | IllegalArgumentException exception) {
            refreshToken.revoke();
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid refresh token.");
        }

        refreshToken.revoke();
        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void sendSignupCode(EmailCodeRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(HttpStatus.CONFLICT, "Email already exists.");
        }

        saveEmailCode(request.email(), EmailVerificationPurpose.SIGNUP);
    }

    @Transactional
    public void sendPasswordResetCode(EmailCodeRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            throw new AuthException(HttpStatus.NOT_FOUND, "Email is not registered.");
        }

        saveEmailCode(request.email(), EmailVerificationPurpose.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "Email is not registered."));

        verifyEmailCode(request.email(), request.verificationCode(), EmailVerificationPurpose.PASSWORD_RESET);
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(Sha256Hash.value(refreshToken))
                .expiresAt(LocalDateTime.now().plusNanos(jwtTokenProvider.getRefreshTokenValidityMs() * 1_000_000))
                .build());

        return new TokenResponse("Bearer", accessToken, refreshToken, jwtTokenProvider.getAccessTokenValidityMs() / 1000);
    }

    private void saveEmailCode(String email, EmailVerificationPurpose purpose) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        emailVerificationCodeRepository.save(EmailVerificationCode.builder()
                .email(email)
                .codeHash(Sha256Hash.value(code))
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(emailCodeValidityMinutes))
                .build());

        emailCodeSender.send(email, purpose, code);
        log.info("Email verification code issued. email={}, purpose={}", email, purpose);
    }

    private void verifyEmailCode(String email, String code, EmailVerificationPurpose purpose) {
        EmailVerificationCode verificationCode = emailVerificationCodeRepository
                .findWithLockTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new AuthException(HttpStatus.BAD_REQUEST, "Email verification code not found."));

        if (!verificationCode.isValid(Sha256Hash.value(code), emailCodeMaxFailedAttempts)) {
            verificationCode.fail();
            throw new AuthException(HttpStatus.BAD_REQUEST, "Email verification code is invalid or expired.");
        }

        verificationCode.use();
    }
}
