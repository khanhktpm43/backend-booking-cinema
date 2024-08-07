package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatTypeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.SeatTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/seat-types")
@CrossOrigin(origins = "*")
public class SeatTypeController {
    @Autowired
    private SeatTypeRepository seatTypeRepository;
    @Autowired
    private SeatTypeService seatTypeService;


    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatType>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<SeatType>> result = seatTypeService.getByDeleted(false, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<SeatType>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<SeatType>> result = seatTypeService.getByDeleted(true, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));

    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> getById(@PathVariable Long id) {
        if (seatTypeRepository.existsById(id)) {
            DetailResponse<SeatType> response = seatTypeService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> create(@RequestBody SeatType seatType, HttpServletRequest request) {
        Example<SeatType> example = Example.of(seatType);
        if (seatTypeRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        DetailResponse<SeatType> response = seatTypeService.create(request, seatType);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> update(@PathVariable Long id, @RequestBody SeatType seatType, HttpServletRequest request) {
        if (seatTypeRepository.existsById(id)) {
            DetailResponse<SeatType> response = seatTypeService.update(request, id, seatType);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> delete(@PathVariable Long id, HttpServletRequest request) {
        if (seatTypeRepository.existsByIdAndDeleted(id, false)) {
            seatTypeService.delete(request, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> restore(@PathVariable Long id, HttpServletRequest request) {
        if (!seatTypeRepository.existsByIdAndDeleted(id, true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        DetailResponse<SeatType> response = seatTypeService.restore(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }
}
