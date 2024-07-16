package com.dev.booking.Service;

import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.TicketRepository;
import com.dev.booking.RequestDTO.TicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Transactional
    public List<Ticket> BookTicket(User user, Showtime showtime, List<TicketDTO> ticketDTOS){
        return null;
    }
}
