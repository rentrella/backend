package com.example.rentrella.auth.service;

import com.example.rentrella.auth.domain.User;
import com.example.rentrella.auth.domain.UserRole;
import com.example.rentrella.auth.dto.LoginRequest;
import com.example.rentrella.auth.dto.TokenResponse;
import com.example.rentrella.auth.exception.AuthException;
import com.example.rentrella.auth.repository.EmailVerificationCodeRepository;
import com.example.rentrella.auth.repository.RefreshTokenRepository;
import com.example.rentrella.auth.repository.UserRepository;
import com.example.rentrella.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private EmailVerificationCodeRepository emailVerificationCodeRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailCodeSender emailCodeSender;

    @Mock
    private DataGsmStudentSyncService dataGsmStudentSyncService;

    @InjectMocks
    private AuthService authService;

    private User newUser(LocalDate endBanned) {
        User user = User.builder()
                .email("user@test.com")
                .password("encoded-password")
                .name("테스트유저")
                .role(UserRole.USER)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "endBanned", endBanned);
        return user;
    }

    @Test
    void 대여금지_기간이_남아있으면_로그인이_거부된다() {
        User bannedUser = newUser(LocalDate.now().plusDays(1));
        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(bannedUser));
        given(passwordEncoder.matches("password", bannedUser.getPassword())).willReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@test.com", "password")))
                .isInstanceOf(AuthException.class)
                .extracting(exception -> ((AuthException) exception).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void 대여금지_기간이_오늘까지면_로그인이_거부된다() {
        User bannedUser = newUser(LocalDate.now());
        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(bannedUser));
        given(passwordEncoder.matches("password", bannedUser.getPassword())).willReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@test.com", "password")))
                .isInstanceOf(AuthException.class)
                .extracting(exception -> ((AuthException) exception).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void 대여금지_기간이_지났으면_로그인이_허용된다() {
        User user = newUser(LocalDate.now().minusDays(1));
        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password", user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(user)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(user)).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenValidityMs()).willReturn(1_000L);
        given(jwtTokenProvider.getAccessTokenValidityMs()).willReturn(1_000L);

        TokenResponse response = authService.login(new LoginRequest("user@test.com", "password"));

        assertThat(response.accessToken()).isEqualTo("access-token");
    }

    @Test
    void 대여금지_이력이_없으면_로그인이_허용된다() {
        User user = newUser(null);
        given(userRepository.findByEmail("user@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password", user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenValidityMs()).willReturn(1_000L);
        given(jwtTokenProvider.getAccessTokenValidityMs()).willReturn(1_000L);

        TokenResponse response = authService.login(new LoginRequest("user@test.com", "password"));

        assertThat(response.accessToken()).isEqualTo("access-token");
    }
}
