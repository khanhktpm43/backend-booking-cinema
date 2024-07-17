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
public class SeatController {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private SeatService seatService;


    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Seat>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Seat> seats = seatRepository.findAllByDeleted(false, pageable);
        Page<DetailResponse<Seat>> result = mappingService.mapToResponse(seats);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Seat>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Seat> seats = seatRepository.findAllByDeleted(false, pageable);
        Page<DetailResponse<Seat>> result = mappingService.mapToResponse(seats);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/room/id")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> getByRoom(@PathVariable Long id){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("call api: api/v1/rooms/{id}/seats", null));

    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> getById(@PathVariable Long id){
        if (seatRepository.existsById(id)) {
            Seat seat = seatRepository.findById(id).orElse(null);
            DetailResponse<Seat> response = mappingService.mapToResponse(seat);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> create(@RequestBody Seat seat, HttpServletRequest request){
        Example<Seat> example = Example.of(seat);
        if (seatRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        seat.setId(null);
        seat.setCreatedBy(userReq);
        seat.setCreatedAt(LocalDateTime.now());
        seat.setUpdatedAt(null);
        Seat newSeat = seatRepository.save(seat);
        DetailResponse<Seat> response = new DetailResponse<>(newSeat, newSeat.getCreatedBy(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @Transactional
    @PostMapping("/room/{roomId}")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> createSeatsByRoom(@PathVariable Long roomId, @RequestBody List<SeatDTO> seatDTOS,  HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        if(!roomRepository.existsById(roomId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("room not found", null));
        if(seatDTOS.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("seats is Empty", null));
        Room room = roomRepository.findById(roomId).orElseThrow();
        List<DetailResponse<Seat>> responses = seatService.createSeats(room,seatDTOS, userReq);
        if (responses.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Seat already exists, transaction rolled back.", null));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> update(@PathVariable Long id,@RequestBody Seat seat, HttpServletRequest request){
        if (seatRepository.existsById(id) ) {
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            Seat seat1 = seatRepository.findById(id).orElseThrow();
            seat1.setRow(seat.getRow());
            seat1.setColumn(seat.getColumn());
            seat1.setRoom(seat.getRoom());
            seat1.setSeatType(seat.getSeatType());
            seat1.setName(seat.getName());
            seat1.setUpdatedAt(LocalDateTime.now());
            seat1.setUpdatedBy(userReq);
            Seat newSeat = seatRepository.save(seat1);
            DetailResponse<Seat> response = new DetailResponse<>(newSeat, newSeat.getCreatedBy(), userReq);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> delete(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if(seatRepository.existsByIdAndDeleted(id, false)){
            Seat seat = seatRepository.findById(id).orElse(null);
            seat.setDeleted(true);
            seat.setUpdatedBy(userReq);
            seat.setUpdatedAt(LocalDateTime.now());
            seatRepository.save(seat);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<Seat>> restore(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        Seat seat = seatRepository.findByIdAndDeleted(id, true).orElse(null);
        if (seat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        seat.setDeleted(false);
        seat.setUpdatedAt(LocalDateTime.now());
        seat.setUpdatedBy(userReq);
        seatRepository.save(seat);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",seat));
    }



}
