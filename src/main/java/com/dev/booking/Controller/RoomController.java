package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.JWT.JwtUtil;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.MovieResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/room")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Room>>>> getAll() {
        List<Room> rooms = roomRepository.findAll();

        List<DetailResponse<Room>> result = rooms.stream().map(room -> {
            UserBasicDTO createdBy = null;
            if (room.getCreatedBy() != null) {
                User user = userRepository.findById(room.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (room.getUpdatedBy() != null) {
                User user = userRepository.findById(room.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(room, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> getById(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            UserBasicDTO createdBy = null;
            UserBasicDTO updatedBy = null;

            Room room = roomRepository.findById(id).orElse(null);
            if (room.getCreatedBy() != null) {
                User user = userRepository.findById(room.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            if (room.getUpdatedBy() != null) {
                User user = userRepository.findById(room.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            DetailResponse<Room> response = new DetailResponse<>(room, createdBy, updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> create(@RequestBody Room room, HttpServletRequest request) {
        Example<Room> example = Example.of(room);
        if (roomRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
        String username = (String) tokenAndUsername.get("username");
        User userReq = userRepository.findByUserName(username).orElseThrow();
        room.setId(null);
        room.setCreatedBy(userReq.getId());
        room.setCreatedAt(LocalDateTime.now());
        Room newRoom = roomRepository.save(room);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<Room> response = new DetailResponse<>(newRoom, createdBy, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> update(@PathVariable Long id, @RequestBody Room room, HttpServletRequest request) {
        if (roomRepository.existsById(id)) {
            Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
            String username = (String) tokenAndUsername.get("username");
            User userReq = userRepository.findByUserName(username).orElseThrow();
            Room room1 = roomRepository.findById(id).orElse(null);
            room1.setCode(room.getCode());
            room1.setName(room.getName());
            room1.setUpdatedAt(LocalDateTime.now());
            room1.setUpdatedBy(userReq.getId());
            Room newRoom = roomRepository.save(room1);
            User user = userRepository.findById(newRoom.getCreatedBy()).orElse(null);
            UserBasicDTO createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse response = new DetailResponse(newRoom, createdBy, updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Room>>> delete(@PathVariable Long id){
        if(roomRepository.existsById(id)){
            roomRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}



