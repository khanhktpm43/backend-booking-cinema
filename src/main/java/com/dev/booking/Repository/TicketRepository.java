package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.RequestDTO.TicketDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT new com.dev.booking.RequestDTO.TicketDTO(t.seat, t.showtime, t.price) FROM Ticket t WHERE t.booking.id = :bookingID")
    List<TicketDTO> findAllByBookingId(@Param("bookingID") Long bookingID);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.booked = false WHERE t.booking = :booking")
    void updateUnpaidTickets(@Param("booking") Booking booking);
    void deleteByBooking(Booking booking);

    @Query("SELECT t FROM Ticket t WHERE t.showtime = :showtime " +
            "AND t.booking != :booking " +
            "AND t.booked = true " +
            "AND t.seat IN (SELECT t.seat FROM Ticket t WHERE t.showtime = :showtime AND t.booking = :booking)")
    List<Ticket> findTicketsByConditions(@Param("showtime") Showtime showtime,
                                         @Param("booking") Booking booking);
}
