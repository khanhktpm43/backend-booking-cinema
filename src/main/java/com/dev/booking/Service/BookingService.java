package com.dev.booking.Service;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import com.dev.booking.ResponseDTO.BillDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MappingService mappingService;

    @Transactional
    public DetailResponse<BillDTO> createBill(BookingDTO bookingDTO, User customer, User createdBy) {
        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setUser(customer);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedBy(createdBy);
        booking.setUpdatedAt(null);
        Booking newBooking = repository.save(booking);
        List<OrderFoodDTO> foods = new ArrayList<>();
        List<CustomerOrder> orders = customerOrderService.orderFood(createdBy, newBooking, bookingDTO.getFoodOrderList());
        float priceFoods = 0;
        for (CustomerOrder order : orders) {
            OrderFoodDTO orderFoodDTO = new OrderFoodDTO(order.getFood(), order.getAmount(), order.getPrice());
            priceFoods += order.getPrice();
            foods.add(orderFoodDTO);
        }
        List<Ticket> tickets = ticketService.BookTicket(createdBy, newBooking, bookingDTO.getShowtime(), bookingDTO.getSeats());
        float priceTickets = 0;
        for (Ticket ticket : tickets) {
            priceTickets += ticket.getPrice();
        }
        float totalPrice = priceFoods + priceTickets;
        newBooking.setTotalPrice(totalPrice);
        newBooking.setTickets(new HashSet<>(tickets));
        newBooking.setCustomerOrders(new HashSet<>(orders));
        repository.save(newBooking);
        BillDTO billDTO = new BillDTO();
        billDTO.setCreatedAt(LocalDateTime.now());
        billDTO.setCustomerName(customer.getName());
        billDTO.setMail(customer.getEmail());
        billDTO.setPhone(customer.getPhone());
        billDTO.setFoods(foods);

        billDTO.setSeats(tickets);
        if (!tickets.isEmpty()) {
            billDTO.setRoom(tickets.get(0).getShowtime().getRoom().getName());
            billDTO.setMovieName(tickets.get(0).getShowtime().getMovie().getName());
            billDTO.setShowtime(tickets.get(0).getShowtime().getStartTime());
        }
        billDTO.setTotalPrice(newBooking.getTotalPrice());
        return new DetailResponse<>(billDTO, createdBy, null, billDTO.getCreatedAt(), null);
    }

    public Page<DetailResponse<Booking>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Booking> bookings = repository.findAll(pageable);
        return mappingService.mapToResponse(bookings);
    }

    public DetailResponse<Booking> getById(Long id) {
        Booking booking = repository.findById(id).orElseThrow();
        return mappingService.mapToResponse(booking);
    }
}
