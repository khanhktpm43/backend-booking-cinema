package com.dev.booking.Repository;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.ResponseDTO.ShowtimeSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Page<Seat> findAllByDeleted(boolean b, Pageable pageable);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<Seat> findByIdAndDeleted(Long id, boolean b);
    @Query("SELECT new com.dev.booking.ResponseDTO.ShowtimeSeat(s, CASE WHEN SUM(CASE WHEN t.booked = true THEN 1 ELSE 0 END) = 0 OR COUNT(t.id) = 0 THEN FALSE ELSE TRUE END ) " +
            "FROM Seat s LEFT JOIN Ticket t ON s.id = t.seat.id " +
            "AND t.showtime = :showtime " +
            "GROUP BY s.id " +
            "HAVING s.room = :room " +
            "ORDER BY s.row ASC, s.column ASC")
    List<ShowtimeSeat> findByShowtime(@Param("showtime") Showtime showtime, @Param("room") Room room);

    boolean existsByRoomAndRowAndColumnAndDeleted(Room room, String row, int column, boolean b);
}
