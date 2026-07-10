package com.example.rentrella.umbrella.repository;

import com.example.rentrella.umbrella.entity.RentalLog;
import com.example.rentrella.umbrella.entity.RentalStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RentalLogRepositoryTest {

    @Autowired
    private RentalLogRepository rentalLogRepository;

    @Test
    void 사용자의_가장_최근_로그를_조회한다() {
        rentalLogRepository.save(new RentalLog(1L, 5L, RentalStatus.BORROW));
        RentalLog latest = rentalLogRepository.save(new RentalLog(1L, 5L, RentalStatus.RETURN));
        rentalLogRepository.save(new RentalLog(2L, 7L, RentalStatus.BORROW));

        assertThat(rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(1L))
                .isPresent()
                .get()
                .extracting(RentalLog::getLogId)
                .isEqualTo(latest.getLogId());
    }

    @Test
    void 로그가_없는_사용자는_빈_값을_반환한다() {
        assertThat(rentalLogRepository.findFirstByUserIdOrderByLogIdDesc(999L)).isEmpty();
    }

    @Test
    void 모든_로그를_최신순으로_조회한다() {
        RentalLog first = rentalLogRepository.save(new RentalLog(1L, 5L, RentalStatus.BORROW));
        RentalLog second = rentalLogRepository.save(new RentalLog(1L, 5L, RentalStatus.RETURN));

        assertThat(rentalLogRepository.findAllByOrderByLogIdDesc())
                .extracting(RentalLog::getLogId)
                .containsExactly(second.getLogId(), first.getLogId());
    }
}
