package com.dev.booking.Controller;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatPriceRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.SeatPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/seat-price")
public class SeatPriceController {
    @Autowired
    private SeatPriceRepository seatPriceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private SeatPriceService seatPriceService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<SeatPrice>>>> getAll(){
        List<SeatPrice> seatPrices = seatPriceRepository.findAll();
        List<DetailResponse<SeatPrice>> responses = seatPriceService.mapToResponse(seatPrices);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> getById(@PathVariable Long id){
        if(seatPriceRepository.existsById(id)){
            DetailResponse<SeatPrice> response = seatPriceService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> create(@RequestBody SeatPrice seatPrice){

        return null;
    }
}
