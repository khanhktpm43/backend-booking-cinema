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
public class SeatPriceController {
    @Autowired
    private SeatPriceRepository seatPriceRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private SeatPriceService seatPriceService;
    @Autowired
    private MappingService mappingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatPrice>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SeatPrice> seatPrices = seatPriceRepository.findAll(pageable);
        Page<DetailResponse<SeatPrice>> responses = mappingService.mapToResponse(seatPrices);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> getById(@PathVariable Long id){
        if(seatPriceRepository.existsById(id)){
            SeatPrice seatPrice = seatPriceRepository.findById(id).orElse(null);
            DetailResponse<SeatPrice> response = mappingService.mapToResponse(seatPrice);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> create(@RequestBody SeatPrice seatPrice, HttpServletRequest request){
        if(seatPriceService.isValid(seatPrice)){
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            LocalDateTime start = LocalDateTime.parse(seatPrice.getStartDate().toString(), formatter);
//            LocalDateTime end = LocalDateTime.parse(seatPrice.getEndDate().toString(), formatter);
            seatPrice.setId(null);
            seatPrice.setCreatedBy(userReq);
            seatPrice.setCreatedAt(LocalDateTime.now());
//            seatPrice.setStartDate(start);
//            seatPrice.setEndDate(end);
            seatPrice.setUpdatedAt(null);
            SeatPrice seatPrice1 = seatPriceRepository.save(seatPrice);
            DetailResponse<SeatPrice> response = new DetailResponse<>(seatPrice1, userReq, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("invalid", null));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatPrice>>> update(@PathVariable Long id,@RequestBody SeatPrice seatPrice, HttpServletRequest request){
       if(seatPriceRepository.existsById(id) && seatPrice.isValid()){
           User userReq = jwtRequestFilter.getUserRequest(request);
           if(userReq == null){
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
           }
           if(seatPriceRepository.checkDuplicateSeatPrice(seatPrice,id))
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
           SeatPrice seatPrice1 = seatPriceRepository.findById(id).orElse(null);
           if(seatPrice1 != null){
               seatPrice1.setPrice(seatPrice.getPrice());
               seatPrice1.setSeatType(seatPrice.getSeatType());
               seatPrice1.setStartDate(seatPrice.getStartDate());
               seatPrice1.setEndDate(seatPrice.getEndDate());
               seatPrice1.setEarlyShow(seatPrice.isEarlyShow());
               seatPrice1.setNormalDay(seatPrice.isNormalDay());
               seatPrice1.setWeekend(seatPrice.isWeekend());
               seatPrice1.setSpecialDay(seatPrice.isSpecialDay());
               seatPrice1.setUpdatedAt(LocalDateTime.now());
               seatPrice1.setUpdatedBy(userReq);
               SeatPrice seatPrice2 =  seatPriceRepository.save(seatPrice1);
               DetailResponse<SeatPrice> response = new DetailResponse<>(seatPrice2, seatPrice2.getCreatedBy(), seatPrice2.getUpdatedBy());
               return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
           }

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
