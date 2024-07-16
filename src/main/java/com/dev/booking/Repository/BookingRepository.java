package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Page<Booking> findAllByDeleted(boolean b , Pageable pageable);
}
