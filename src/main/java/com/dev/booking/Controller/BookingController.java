package com.dev.booking.Controller;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.PaymentMethod;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.ResponseDTO.BookingResponse;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.PaymentResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private BookingService bookingService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Booking>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Booking>> responses = bookingService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('GUEST')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<BookingResponse>>> getById(@PathVariable Long id){
        if(!bookingRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        DetailResponse<BookingResponse> response = bookingService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }

    @PreAuthorize("hasRole('GUEST')")
    @GetMapping("/User")
    public ResponseEntity<ResponseObject<List<BookingResponse>>> getByUser(HttpServletRequest request){
        List<BookingResponse> responses = bookingService.getByUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }

    @PreAuthorize("hasRole('GUEST')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<PaymentResponse>> booking(@RequestBody BookingDTO booking, HttpServletRequest request) throws Exception {
        User user = jwtRequestFilter.getUserRequest(request);
        PaymentResponse response = bookingService.payment(booking,user, user, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/direct-payment")
    public ResponseEntity<ResponseObject<PaymentResponse>> booking(@RequestParam(defaultValue = "") String phone, @RequestParam(defaultValue = "CASH") PaymentMethod method, @RequestBody BookingDTO booking, HttpServletRequest request) throws Exception {
        User user = jwtRequestFilter.getUserRequest(request);
        PaymentResponse response = bookingService.directPayment(phone, method,booking, user, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('GUEST')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject<Map<String, String>>> retryPayment(@PathVariable Long id, HttpServletRequest request) throws Exception {
        Map<String, String> response = new HashMap<>();
        if(!bookingRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("booking id does not exist", null));
        String paymentURL = bookingService.retryPayment(request, id);
        if(paymentURL == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("ticket not available", null));
        response.put("paymentURL", paymentURL);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }

    @PreAuthorize("hasRole('GUEST')")
    @GetMapping("/return")
    public ResponseEntity<ResponseObject<BookingResponse>> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException {
        BookingResponse response = bookingService.getBooking(request);
        if(response != null)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("payment failed", null));
    }

}
