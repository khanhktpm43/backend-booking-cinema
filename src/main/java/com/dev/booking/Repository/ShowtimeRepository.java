package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("SELECT CASE WHEN COUNT(st) = 0 THEN true ELSE false END FROM Showtime st WHERE st.room = :#{#showtime.room} AND ((st.startTime <= :#{#showtime.startTime} AND st.endTime >= :#{#showtime.startTime}) OR (st.startTime <= :#{#showtime.endTime} AND st.endTime >= :#{#showtime.endTime}))")
    boolean isValid(@Param("showtime") Showtime showtime);
    @Query("SELECT CASE WHEN COUNT(st) = 0 THEN false ELSE true END   FROM Showtime st WHERE st.room = :#{#showtime.room} AND ((st.startTime <= :#{#showtime.startTime} AND st.endTime >= :#{#showtime.startTime}) OR (st.startTime <= :#{#showtime.endTime} AND st.endTime >= :#{#showtime.endTime})) AND st.id <> :currentId")
    boolean checkDuplicate(@Param("showtime") Showtime showtime, @Param("currentId") Long currentId);
    @Query(value = "SELECT s.movieid, GROUP_CONCAT(CONCAT(s.id, '-', DATE_FORMAT(s.start_time, '%H:%i')) SEPARATOR '|')  " +
            "FROM showtime s " +
            "WHERE DATE(s.start_time) = :date " +
            "GROUP BY s.movieid", nativeQuery = true)
    List<Object[]> findShowtimesByDate(@Param("date") LocalDate date);
}
