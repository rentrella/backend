package com.example.rentrella.umbrella.repository;

import com.example.rentrella.umbrella.entity.Umbrella;
import com.example.rentrella.umbrella.entity.UmbrellaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UmbrellaRepositoryTest {

    @Autowired
    private UmbrellaRepository umbrellaRepository;

    @Test
    void deviceId로_우산을_조회할_수_있다() {
        umbrellaRepository.save(new Umbrella(1L, UmbrellaStatus.AVAILABLE));

        assertThat(umbrellaRepository.findByDeviceId(1L))
                .isPresent()
                .get()
                .extracting(Umbrella::getStatus)
                .isEqualTo(UmbrellaStatus.AVAILABLE);
    }

    @Test
    void 존재하지_않는_deviceId면_빈_값을_반환한다() {
        assertThat(umbrellaRepository.findByDeviceId(999L)).isEmpty();
    }

    @Test
    void 상태별_우산_개수를_센다() {
        umbrellaRepository.save(new Umbrella(1L, UmbrellaStatus.AVAILABLE));
        umbrellaRepository.save(new Umbrella(2L, UmbrellaStatus.AVAILABLE));
        umbrellaRepository.save(new Umbrella(3L, UmbrellaStatus.RENTED));

        assertThat(umbrellaRepository.countByStatus(UmbrellaStatus.AVAILABLE)).isEqualTo(2L);
        assertThat(umbrellaRepository.countByStatus(UmbrellaStatus.RENTED)).isEqualTo(1L);
    }
}
