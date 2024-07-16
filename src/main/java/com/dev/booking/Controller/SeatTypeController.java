package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatTypeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/seat-types")
public class SeatTypeController {
    @Autowired
    private SeatTypeRepository seatTypeRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MappingService mappingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatType>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SeatType> seatTypes = seatTypeRepository.findAllByDeleted(false, pageable);
        Page<DetailResponse<SeatType>> result = mappingService.mapToResponse(seatTypes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatType>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SeatType> seatTypes = seatTypeRepository.findAllByDeleted(true, pageable);
        Page<DetailResponse<SeatType>> result = mappingService.mapToResponse(seatTypes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));

    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> getById(@PathVariable Long id){
        if (seatTypeRepository.existsById(id)) {
            SeatType type = seatTypeRepository.findById(id).orElse(null);

            DetailResponse<SeatType> response = mappingService.mapToResponse(type);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> create(@RequestBody SeatType seatType, HttpServletRequest request){
        Example<SeatType> example = Example.of(seatType);
        if (seatTypeRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        seatType.setId(null);
        seatType.setCreatedBy(userReq);
        seatType.setCreatedAt(LocalDateTime.now());
        seatType.setUpdatedAt(null);
        SeatType newType = seatTypeRepository.save(seatType);

        DetailResponse<SeatType> response = new DetailResponse<>(newType, newType.getCreatedBy(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> update(@PathVariable Long id,@RequestBody SeatType seatType, HttpServletRequest request){
        if (seatTypeRepository.existsById(id)) {
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            SeatType type = seatTypeRepository.findById(id).orElse(null);
            type.setCode(seatType.getCode());
            type.setName(seatType.getName());
            type.setUpdatedAt(LocalDateTime.now());
            type.setUpdatedBy(userReq);
            SeatType newType = seatTypeRepository.save(type);

            DetailResponse<SeatType> response = new DetailResponse<>(newType, newType.getCreatedBy(), newType.getUpdatedBy());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> delete(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if(seatTypeRepository.existsByIdAndDeleted(id, false)){
            SeatType seatType = seatTypeRepository.findById(id).orElse(null);
            seatType.setDeleted(true);
            seatType.setUpdatedBy(userReq);
            seatType.setUpdatedAt(LocalDateTime.now());
            seatTypeRepository.save(seatType);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<SeatType>> restore(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        SeatType seatType = seatTypeRepository.findByIdAndDeleted(id, true).orElse(null);
        if (seatType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        seatType.setDeleted(false);
        seatType.setUpdatedAt(LocalDateTime.now());
        seatType.setUpdatedBy(userReq);
        seatTypeRepository.save(seatType);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",seatType));
    }
}
