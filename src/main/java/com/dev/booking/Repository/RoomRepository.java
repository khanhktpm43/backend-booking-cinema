package com.dev.booking.Repository;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findByDeleted(boolean b, Pageable pageable);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<Room> findByIdAndDeleted(Long id, boolean b);
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId AND s.deleted = false ORDER BY s.row ASC, s.column ASC")
    List<Seat> findSeatsInRoomSortedByRowAndColumn(Long roomId);
}
