package com.dev.booking.Service;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public Page<DetailResponse<Room>> getByDeleted(boolean b, int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Room> rooms = roomRepository.findByDeleted(b, pageable);
        return mappingService.mapToResponse(rooms);
    }

    public DetailResponse<Room> getById(Long id) {
        Room room = roomRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(room);
    }

    public List<DetailResponse<Seat>> getSeats(Long id) {
        List<Seat> sortedSeats = roomRepository.findSeatsInRoomSortedByRowAndColumn(id);
        return mappingService.mapToResponse(sortedSeats);
    }

    public DetailResponse<Room> create(HttpServletRequest request, Room room) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        room.setId(null);
        room.setCreatedBy(userReq);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(null);
        Room newRoom = roomRepository.save(room);
        return mappingService.mapToResponse(newRoom);
    }

    public DetailResponse<Room> update(HttpServletRequest request, Long id, Room room) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Room room1 = roomRepository.findById(id).orElseThrow();
        room1.setCode(room.getCode());
        room1.setName(room.getName());
        room1.setUpdatedAt(LocalDateTime.now());
        room1.setUpdatedBy(userReq);
        Room newRoom = roomRepository.save(room1);
        return mappingService.mapToResponse(newRoom);
    }

    public void delete(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Room room = roomRepository.findById(id).orElseThrow();
        room.setDeleted(true);
        room.setUpdatedBy(userReq);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
    }

    public Room restore(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Room room = roomRepository.findByIdAndDeleted(id, true).orElseThrow();
        room.setDeleted(false);
        room.setUpdatedAt(LocalDateTime.now());
        room.setUpdatedBy(userReq);
        return roomRepository.save(room);
    }
}
