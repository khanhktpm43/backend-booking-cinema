package com.dev.booking.Controller;

import com.dev.booking.Entity.Booking;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
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

import java.util.List;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private MappingService mappingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Booking>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Booking> bookings = bookingRepository.findAllByDeleted(false,pageable);
        Page<DetailResponse<Booking>> responses = mappingService.mapToResponse(bookings);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Booking>>>> getAllDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Booking> bookings = bookingRepository.findAllByDeleted(true, pageable);
        Page<DetailResponse<Booking>> responses = mappingService.mapToResponse(bookings);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    //get detail by id

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Booking>>> bookingOnline(@RequestBody Booking booking, HttpServletRequest request){
        return null;
    }
}
