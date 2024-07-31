package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,Long> {
    @Query("SELECT new com.dev.booking.RequestDTO.OrderFoodDTO(co.food, co.amount, co.price) FROM CustomerOrder co WHERE co.booking.id = :bookingID")
    List<OrderFoodDTO> findAllByBookingId(@Param("bookingID") Long bookingID);

    void deleteByBooking(Booking booking);
}
