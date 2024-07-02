package com.dev.booking.Controller;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/seat")
public class SeatController {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private SeatService seatService;
    @Autowired
    private RoomRepository roomRepository;
    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> getAll(){
        List<Seat> seats = seatRepository.findAll();
        List<DetailResponse<Seat>> result = seatService.mapSeatToSeatResponse(seats);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/room/id")
    public ResponseEntity<ResponseObject<List<DetailResponse<Seat>>>> getByRoom(@PathVariable Long id){
        if(!roomRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        Room room = roomRepository.findById(id).orElseThrow();
        List<Seat> seats = seatRepository.findByRoom(room);
        List<DetailResponse<Seat>> result = seatService.mapSeatToSeatResponse(seats);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> getById(@PathVariable Long id){
        if (seatRepository.existsById(id)) {
            UserBasicDTO createdBy = null;
            UserBasicDTO updatedBy = null;

            Seat seat = seatRepository.findById(id).orElse(null);
            if (seat!= null && seat.getCreatedBy() != null) {
                User user = userRepository.findById(seat.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            if (seat!= null && seat.getUpdatedBy() != null) {
                User user = userRepository.findById(seat.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            DetailResponse<Seat> response = new DetailResponse<>(seat, createdBy, updatedBy);
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
        Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
        String username = (String) tokenAndUsername.get("username");
        User userReq = userRepository.findByUserName(username).orElse(null);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        seat.setId(null);
        seat.setCreatedBy(userReq.getId());
        seat.setCreatedAt(LocalDateTime.now());
        seat.setUpdatedAt(null);
        Seat newSeat = seatRepository.save(seat);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<Seat> response = new DetailResponse<>(newSeat, createdBy, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> update(@PathVariable Long id,@RequestBody Seat seat, HttpServletRequest request){
        if (seatRepository.existsById(id) ) {
            Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
            String username = (String) tokenAndUsername.get("username");
            User userReq = userRepository.findByUserName(username).orElse(null);
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
            seat1.setUpdatedBy(userReq.getId());
            Seat newSeat = seatRepository.save(seat1);
            User user = userRepository.findById(newSeat.getCreatedBy()).orElse(null);
            UserBasicDTO createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<Seat> response = new DetailResponse<>(newSeat, createdBy, updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Seat>>> delete(@PathVariable Long id){
        if(seatRepository.existsById(id)){
            seatRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }



}
