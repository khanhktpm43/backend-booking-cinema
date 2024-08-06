package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.RequestDTO.TicketDTO;
import org.springframework.cglib.core.Local;
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
    @Query("UPDATE Ticket t SET t.booked = :status WHERE t.booking = :booking")
    void changeStatusBookedTickets(@Param("booking") Booking booking, @Param("status") boolean status);
    void deleteByBooking(Booking booking);
    @Query("SELECT t FROM Ticket t WHERE t.showtime IN (SELECT t4.showtime FROM Ticket t4 WHERE t4.booking = :booking ) " +
            "AND t.booking != :booking " +
            "AND t.booked = true " +
            "AND t.showtime IN (SELECT t2.showtime FROM Ticket t2 WHERE t2.booking = :booking ) " +
            "AND t.seat IN (SELECT t3.seat FROM Ticket t3 WHERE t3.booking = :booking)")
    List<Ticket> findTicketsByConditions(@Param("booking") Booking booking);

    @Query("SELECT t FROM Ticket t WHERE t.showtime.startTime < :dateTime  " +
            "AND t.booking = :booking ")
    List<Ticket> findTicketInvalid(@Param("booking") Booking booking, @Param("dateTime") LocalDateTime dateTime);
}
