package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
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
    @Query("DELETE FROM Ticket t WHERE t.booking.id IN " +
            "(SELECT b.id FROM Booking b WHERE b.bookingDate <= :cutoffDateTime AND b.transactionId IS NULL)")
    void deleteUnpaidTickets(@Param("cutoffDateTime") LocalDateTime cutoffDateTime);

    void deleteByBooking(Booking booking);
}
