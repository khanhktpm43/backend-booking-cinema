package com.dev.booking.Service;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.Entity.Ticket;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import com.dev.booking.RequestDTO.TicketDTO;
import com.dev.booking.ResponseDTO.BillDTO;
import com.dev.booking.ResponseDTO.BookingResponse;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public PaymentResponse payment(BookingDTO bookingDTO, User customer, User createdBy, String ip) throws Exception {
        synchronized (bookingDTO.getShowtime()){
            Booking booking = new Booking();
            booking.setBookingDate(LocalDateTime.now());
            booking.setUser(customer);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setCreatedBy(createdBy);
            booking.setUpdatedAt(null);
            booking.setTransactionId(null);
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
            repository.save(newBooking);
            BookingResponse bookingResponse = new BookingResponse(newBooking, seats,foods) ;
           String url = vnPayService.createPaymentUrl(newBooking.getId().toString(),(long) newBooking.getTotalPrice(), ip );
            return new PaymentResponse(bookingResponse, url);
        }
    }
    public BookingResponse update(Booking booking, Long transactionID){
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setUpdatedBy(booking.getCreatedBy());
        booking.setTransactionId(transactionID);
        Booking booking1 =  repository.save(booking);
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
        return new DetailResponse<BookingResponse>(bookingResponse,booking.getCreatedBy(), booking.getUpdatedBy(), booking.getCreatedAt(), booking.getUpdatedAt());
    }

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
        //String amount = vnpParams.get("vnp_Amount");
        Booking booking = repository.findById(txnRef).orElseThrow();
        if ("00".equals(responseCode)) {
            return update(booking, Long.valueOf(transactionNo));
        }
        deleteBookingDetail(booking);
        return null;
    }
    public void deleteBookingDetail(Booking booking){
        ticketService.deleteByBooking(booking);
        customerOrderService.deletedByBooking(booking);
    }
}
