package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.SeatDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/seats")
@CrossOrigin(origins = "*")
public class SeatController {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private SeatService seatService;


    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Seat>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Seat>> result = seatService.getByDeleted(false, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Seat>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Seat>> result = seatService.getByDeleted(true, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }

    @GetMapping("/room/id")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> getByRoom(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("call api: api/v1/rooms/{id}/seats", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> getById(@PathVariable Long id) {
        if (seatRepository.existsById(id)) {
            DetailResponse<Seat> response = seatService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> create(@RequestBody Seat seat, HttpServletRequest request) {
        Example<Seat> example = Example.of(seat);
        if (seatRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        Seat newSeat = seatService.create(request, seat);
        DetailResponse<Seat> response = mappingService.mapToResponse(newSeat);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @Transactional
    @PostMapping("/room/{roomId}")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> createSeatsByRoom(@PathVariable Long roomId, @RequestBody List<SeatDTO> seatDTOS, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("call post api api/v1/rooms/{roomId}/seats", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> update(@PathVariable Long id, @RequestBody Seat seat, HttpServletRequest request) {
        if (seatRepository.existsById(id)) {
            DetailResponse<Seat> response = seatService.update(request, id, seat);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> delete(@PathVariable Long id, HttpServletRequest request) {
        if (seatRepository.existsByIdAndDeleted(id, false)) {
            seatService.delete(request, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> restore(@PathVariable Long id, HttpServletRequest request) {
        if (!seatRepository.existsByIdAndDeleted(id, true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        DetailResponse<Seat> response = seatService.restore(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }


}
