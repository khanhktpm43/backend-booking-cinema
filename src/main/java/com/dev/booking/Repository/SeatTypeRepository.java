package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
    List<SeatType> findAllByDeleted(boolean b);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<SeatType> findByIdAndDeleted(Long id, boolean b);
}
