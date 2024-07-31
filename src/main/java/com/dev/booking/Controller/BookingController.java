package com.dev.booking.Controller;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.ResponseDTO.BookingResponse;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.PaymentResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.BookingService;
import com.dev.booking.Service.MappingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private BookingService bookingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Booking>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Booking>> responses = bookingService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<BookingResponse>>> getById(@PathVariable Long id){
        if(!bookingRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        DetailResponse<BookingResponse> response = bookingService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }
    @GetMapping("/User")
    public ResponseEntity<ResponseObject<List<BookingResponse>>> getByUser(HttpServletRequest request){
        List<BookingResponse> responses = bookingService.getByUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<PaymentResponse>> booking(@RequestBody BookingDTO booking, HttpServletRequest request) throws Exception {
        User user = jwtRequestFilter.getUserRequest(request);
        PaymentResponse response = bookingService.payment(booking,user, user, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @GetMapping("/return")
    public ResponseEntity<ResponseObject<BookingResponse>> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException {
        BookingResponse response = bookingService.getBooking(request);
        if(response != null)
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("payment failed", null));
    }

}
