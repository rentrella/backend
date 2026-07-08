package com.example.rentrella.admin.repository;

import com.example.rentrella.admin.entity.UserBan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserBanRepositoryTest {

    @Autowired
    private UserBanRepository userBanRepository;

    @Test
    void 유저_아이디로_금지_기록을_조회한다() {
        UserBan saved = userBanRepository.save(new UserBan(1L, LocalDateTime.now().plusDays(7)));

        assertThat(userBanRepository.findByUserId(1L))
                .isPresent()
                .get()
                .extracting(UserBan::getId)
                .isEqualTo(saved.getId());
    }

    @Test
    void 금지_기록이_없는_유저는_빈_값을_반환한다() {
        assertThat(userBanRepository.findByUserId(999L)).isEmpty();
    }
}
