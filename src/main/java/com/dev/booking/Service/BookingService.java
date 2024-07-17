package com.dev.booking.Service;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.ResponseDTO.BillDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private TicketService ticketService;
    @Transactional
    public DetailResponse<BillDTO> createBill(BookingDTO bookingDTO, User customer, User createdBy){
        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setUser(customer);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedBy(createdBy);
        booking.setUpdatedAt(null);
        Booking newBooking = repository.save(booking);
        List<CustomerOrder> orders = customerOrderService.orderFood(createdBy, newBooking, bookingDTO.getFoodOrderList());
        float priceFoods =0;
        for (CustomerOrder order : orders) {
            priceFoods += order.getPrice();
        }
        List<Ticket> tickets = ticketService.BookTicket(createdBy,newBooking, bookingDTO.getShowtime(),bookingDTO.getSeats());
        float priceTickets =0;
        for (Ticket ticket : tickets) {
            priceTickets += ticket.getPrice();
        }
        float totalPrice = priceFoods + priceTickets;
        newBooking.setTotalPrice(totalPrice);
        newBooking.setTickets(new HashSet<>(tickets));
        newBooking.setCustomerOrders(new HashSet<>(orders));
        repository.save(newBooking);
        return null;
    }
}
