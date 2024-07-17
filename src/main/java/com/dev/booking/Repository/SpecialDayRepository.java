package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.SpecialDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDay, Long> {
    @Query("SELECT e FROM SpecialDay e WHERE MONTH(e.start) = :month AND YEAR(e.start) = :year")
    Page<SpecialDay> findByMonthAndYear(@Param("month") int month, @Param("year") int year , Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(sd) = 1 THEN 1 ELSE 0 END FROM SpecialDay sd WHERE sd.start <= :#{#showtime.startTime} AND sd.end > :#{#showtime.startTime}")
    int isSpecialDay(@Param("showtime") Showtime showtime);
}
