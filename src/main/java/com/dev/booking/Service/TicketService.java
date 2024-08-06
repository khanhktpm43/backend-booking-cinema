package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.TicketRepository;

import com.dev.booking.RequestDTO.TicketDTO;
import com.dev.booking.ResponseDTO.ShowtimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SeatPriceService seatPriceService;

    @Transactional
    public List<Ticket> BookTicket(User user, Booking booking, Showtime showtime, List<Seat> seats) {
        List<Ticket> tickets = new ArrayList<>();
        for (Seat item : seats) {
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(showtime);
            ticket.setSeat(item);
            ticket.setPrice(seatPriceService.getPrice(showtime, item));
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setUpdatedAt(null);
            ticket.setCreatedBy(user);
            Ticket createdTicket = ticketRepository.save(ticket);
            tickets.add(createdTicket);
        }
        return tickets;
    }
    public boolean canRetryPayment(Booking booking){
        List<Ticket> tickets = ticketRepository.findTicketsByConditions(booking);
        List<Ticket> tickets1 = ticketRepository.findTicketInvalid(booking, LocalDateTime.now());
        return tickets.isEmpty() && tickets1.isEmpty();
    }

    public List<TicketDTO> getDTOByBookingId(Long id) {
        return ticketRepository.findAllByBookingId(id);
    }



    public void changeActiveTickets(Booking booking, boolean status){
        ticketRepository.changeStatusBookedTickets(booking, status);
    }
}
