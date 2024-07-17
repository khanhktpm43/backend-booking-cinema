package com.dev.booking.Controller;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.SpecialDay;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SpecialDayRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.SpecialDayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/special-days")
public class SpecialDayController {
    @Autowired
    private SpecialDayRepository specialDayRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MappingService mappingService;
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SpecialDay>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SpecialDay> specialDays = specialDayRepository.findAll(pageable);
        Page<DetailResponse<SpecialDay>> result = mappingService.mapToResponse(specialDays);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/month-year")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SpecialDay>>>> getByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "start,asc") String[] sort
    ){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SpecialDay> specialDays = specialDayRepository.findByMonthAndYear(month, year, pageable);
        Page<DetailResponse<SpecialDay>> result = mappingService.mapToResponse(specialDays);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SpecialDay>>> getById(@PathVariable Long id){
        if (specialDayRepository.existsById(id)) {
            SpecialDay specialDay = specialDayRepository.findById(id).orElse(null);
            DetailResponse<SpecialDay> response = mappingService.mapToResponse(specialDay);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SpecialDay>>> create(@RequestBody SpecialDay specialDay, HttpServletRequest request){
        Example<SpecialDay> example = Example.of(specialDay);
        if (specialDayRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        specialDay.setId(null);
        specialDay.setCreatedBy(userReq);
        specialDay.setCreatedAt(LocalDateTime.now());
        specialDay.setUpdatedAt(null);
        SpecialDay newSpecialDay = specialDayRepository.save(specialDay);

        DetailResponse<SpecialDay> response = new DetailResponse<>(newSpecialDay, newSpecialDay.getCreatedBy(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SpecialDay>>> update(@PathVariable Long id,@RequestBody SpecialDay specialDay, HttpServletRequest request){
        if (specialDayRepository.existsById(id) ) {
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            SpecialDay day = specialDayRepository.findById(id).orElseThrow();
            day.setName(specialDay.getName());
            day.setStart(specialDay.getStart());
            day.setEnd(specialDay.getEnd());
            day.setUpdatedAt(LocalDateTime.now());
            day.setUpdatedBy(userReq);
            SpecialDay newday = specialDayRepository.save(day);
//
            DetailResponse<SpecialDay> response = new DetailResponse<>(newday, newday.getCreatedBy(), newday.getUpdatedBy());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SpecialDay>>> delete(@PathVariable Long id){
        if(specialDayRepository.existsById(id)){
            specialDayRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}
