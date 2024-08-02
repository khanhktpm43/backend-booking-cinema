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
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.SeatPriceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/seat-prices")
@CrossOrigin(origins = "*")
public class SeatPriceController {
    @Autowired
    private SeatPriceRepository seatPriceRepository;
    @Autowired
    private SeatPriceService seatPriceService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatPrice>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<SeatPrice>> responses = seatPriceService.getAll(page, size, sort);
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
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> create(@RequestBody SeatPrice seatPrice, HttpServletRequest request){
        if(seatPriceService.isValid(seatPrice)){
            DetailResponse<SeatPrice> response = seatPriceService.create(request, seatPrice);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("invalid", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> update(@PathVariable Long id,@RequestBody SeatPrice seatPrice, HttpServletRequest request){
       if(seatPriceRepository.existsById(id) && seatPrice.isValid()){
           if(seatPriceRepository.checkDuplicateSeatPrice(seatPrice, id))
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
           DetailResponse<SeatPrice> response = seatPriceService.update(request, id, seatPrice);
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
       }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("id does not exist or dayType invalid", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> delete(@PathVariable Long id){
        if(seatPriceRepository.existsById(id)){
            SeatPrice seatPrice = seatPriceRepository.findById(id).orElse(null);
            if(seatPrice != null && LocalDateTime.now().isAfter(seatPrice.getStartDate()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Cannot delete expired price", null));
            seatPriceRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}
