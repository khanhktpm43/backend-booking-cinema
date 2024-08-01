package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.User;
import com.dev.booking.ResponseDTO.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByUserOrderByBookingDateDesc(User user);
}
