package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.RequestDTO.TicketDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT new com.dev.booking.RequestDTO.TicketDTO(t.seat, t.showtime, t.price) FROM Ticket t WHERE t.booking.id = :bookingID")
    List<TicketDTO> findAllByBookingId(@Param("bookingID") Long bookingID);

    void deleteByBooking(Booking booking);
}
