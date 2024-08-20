package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.JWT.JwtUtil;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.SeatDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.MovieResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.RoomService;
import com.dev.booking.Service.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/rooms")
@CrossOrigin(origins = "*")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SeatService seatService;
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Room>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Room>> result = roomService.getByDeleted(false, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Room>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Room>> result = roomService.getByDeleted(true, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> getById(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            DetailResponse<Room> response = roomService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @GetMapping("/{id}/seats")
    public  ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> getSeatByRoom(@PathVariable Long id){
        if(!roomRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id room does not exist", null));
        List<DetailResponse<Seat>> response = roomService.getSeats(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    @PostMapping("/{roomId}/seats")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> createSeatsByRoom(@PathVariable Long roomId, @RequestBody List<SeatDTO> seatDTOS, HttpServletRequest request){
        if(!roomRepository.existsById(roomId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("room not found", null));
        if(seatDTOS.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("seats is Empty", null));
        List<DetailResponse<Seat>> responses = seatService.createSeatsByRoom(request, roomId, seatDTOS);
        if (responses.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Seat already exists, transaction rolled back.", null));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> create(@RequestBody Room room, HttpServletRequest request) {
        Example<Room> example = Example.of(room);
        if (roomRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        DetailResponse<Room> response =roomService.create(request, room);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> update(@PathVariable Long id, @RequestBody Room room, HttpServletRequest request) {
        if (roomRepository.existsById(id)) {
            DetailResponse<Room> response = roomService.update(request, id, room);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> delete(@PathVariable Long id, HttpServletRequest request){
        if(roomRepository.existsByIdAndDeleted(id, false)){
            roomService.delete(request, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<Room>> restore(@PathVariable Long id, HttpServletRequest request){
        if (!roomRepository.existsByIdAndDeleted(id, true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        Room room = roomService.restore(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",room));
    }
}



