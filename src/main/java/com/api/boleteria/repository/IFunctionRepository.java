package com.api.boleteria.repository;

import com.api.boleteria.model.ScreenType;
import com.api.boleteria.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IFunctionRepository extends JpaRepository<Function, Long> {
    boolean existsByCinemaIdAndShowtime(Long cinemaId, LocalDateTime showtime);
    List<Function> findByCinemaId(Long cinemaId);
    List<Function> findByMovieIdAndAvailableCapacityGreaterThanAndShowtimeAfter(Long movieId, int availableCapacity, LocalDateTime date);
    List<Function> findByCinema_ScreenTypeAndAvailableCapacityGreaterThanAndShowtimeAfter(
            ScreenType screenType,
            int minCapacidad,
            LocalDateTime fechaActual
    );
}
