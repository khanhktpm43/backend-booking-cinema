package com.dev.booking.Repository;

import com.dev.booking.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByDeleted(boolean b);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<Room> findByIdAndDeleted(Long id, boolean b);
}
