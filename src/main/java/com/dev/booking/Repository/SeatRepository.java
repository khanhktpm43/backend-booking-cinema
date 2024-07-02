package com.dev.booking.Repository;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoom(Room room);
}
