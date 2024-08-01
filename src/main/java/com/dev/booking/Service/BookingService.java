package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.Event.BookingEvent;
import com.dev.booking.Event.BookingHandler;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.Repository.CustomerOrderRepository;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.TicketRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import com.dev.booking.RequestDTO.TicketDTO;
import com.dev.booking.ResponseDTO.BillDTO;
import com.dev.booking.ResponseDTO.BookingResponse;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.PaymentResponse;
import com.dev.booking.TestEvent.DoorBellEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private BookingHandler bookingHandler;
    @Transactional
    public PaymentResponse payment(BookingDTO bookingDTO, User customer, User createdBy, String ip) throws Exception {
        bookingDTO.setShowtime(showtimeRepository.findById(bookingDTO.getShowtime().getId()).get());
        synchronized (bookingDTO.getShowtime()) {
            Booking booking = new Booking();
            booking.setBookingDate(LocalDateTime.now());
            booking.setUser(customer);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setCreatedBy(createdBy);
            booking.setUpdatedAt(null);
            booking.setPaymentMethod(PaymentMethod.VN_PAY);
            booking.setPaymentStatus(PaymentStatus.PENDING);
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
            List<TicketDTO> seats = new ArrayList<>();
            float priceTickets = 0;
            for (Ticket ticket : tickets) {
                TicketDTO ticketDTO = new TicketDTO(ticket.getSeat(), ticket.getShowtime(), ticket.getPrice());
                priceTickets += ticket.getPrice();
                seats.add(ticketDTO);
            }
            float totalPrice = priceFoods + priceTickets;
            newBooking.setTotalPrice(totalPrice);
            Booking booking1 = repository.save(newBooking);
            BookingResponse bookingResponse = new BookingResponse(booking1, seats, foods);
            String url = vnPayService.createPaymentUrl(booking1.getId().toString(), (long) booking1.getTotalPrice(), ip);
            applicationEventPublisher.publishEvent(new BookingEvent(this, booking1.getId() ));
            return new PaymentResponse(bookingResponse, url);
        }
    }

    public BookingResponse paymentSuccess(Booking booking) {
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setUpdatedBy(null);
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        Booking booking1 = repository.save(booking);
        return new BookingResponse(booking1, ticketService.getDTOByBookingId(booking1.getId()), customerOrderService.getDTOByBookingId(booking1.getId()));
    }
    public BookingResponse paymentFailed(Booking booking){
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setUpdatedBy(null);
        booking.setPaymentStatus(PaymentStatus.FAILED);
        Booking booking1 = repository.save(booking);
        ticketService.changeActiveTickets(booking1);
        customerOrderService.changeActiveOrders(booking1);
        return new BookingResponse(booking1, ticketService.getDTOByBookingId(booking1.getId()), customerOrderService.getDTOByBookingId(booking1.getId()));
    }

    public Page<DetailResponse<Booking>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Booking> bookings = repository.findAll(pageable);
        return mappingService.mapToResponse(bookings);
    }

    public DetailResponse<BookingResponse> getById(Long id) {
        Booking booking = repository.findById(id).orElseThrow();
        BookingResponse bookingResponse = new BookingResponse(booking, ticketService.getDTOByBookingId(booking.getId()), customerOrderService.getDTOByBookingId(booking.getId()));
        return new DetailResponse<>(bookingResponse, booking.getCreatedBy(), booking.getUpdatedBy(), booking.getCreatedAt(), booking.getUpdatedAt());
    }

@Transactional
    public BookingResponse getBooking(HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            vnpParams.put(paramName, paramValue);
        }
        String responseCode = vnpParams.get("vnp_ResponseCode");
        String transactionNo = vnpParams.get("vnp_TransactionNo");
        Long txnRef = Long.valueOf(vnpParams.get("vnp_TxnRef"));
        Booking booking = repository.findById(txnRef).orElseThrow();
        if ("00".equals(responseCode)) {
            bookingHandler.cancelEvent(booking.getId());
            return paymentSuccess(booking);
        }
        return paymentFailed(booking);
    }



    public List<BookingResponse> getByUser(HttpServletRequest request) {
        User user = jwtRequestFilter.getUserRequest(request);
        List<Booking> bookings = repository.findByUserOrderByBookingDateDesc(user);
        List<BookingResponse> responses = new ArrayList<>();
        for(Booking booking : bookings){
            System.out.println(booking.getTickets());
            BookingResponse bookingResponse = new BookingResponse(booking,ticketService.getDTOByBookingId(booking.getId()), customerOrderService.getDTOByBookingId(booking.getId()));
            responses.add(bookingResponse);
        }
        return responses;
    }

    public String retryPayment(HttpServletRequest request, Long id) {
        User user = jwtRequestFilter.getUserRequest(request);
        String ip = request.getRemoteAddr();
        Booking booking = repository.findById(id).orElseThrow();
        return null;
    }
}
