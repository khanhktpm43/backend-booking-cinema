package com.dev.booking.Repository;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoom(Room room);

    Page<Seat> findAllByDeleted(boolean b, Pageable pageable);

    List<Seat> findByRoomAndDeleted(Room room, boolean b);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<Seat> findByIdAndDeleted(Long id, boolean b);
}
