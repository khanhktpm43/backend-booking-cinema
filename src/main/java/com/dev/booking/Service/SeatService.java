package com.dev.booking.Service;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.SeatDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private RoomRepository roomRepository;

    public List<DetailResponse<Seat>> createSeats(Room room, List<SeatDTO> seatDTOS, User userReq) {
        List<DetailResponse<Seat>> responses = new ArrayList<>();
        for (SeatDTO seatDTO : seatDTOS) {
            Seat seat = new Seat();
            seat.setRow(seatDTO.getRow());
            seat.setRoom(room);
            seat.setColumn(seatDTO.getColumn());
            if (seatRepository.existsByRoomAndRowAndColumnAndDeleted(room, seatDTO.getRow(), seatDTO.getColumn(), false)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return responses;
            }
            seat.setId(null);
            seat.setName(seatDTO.getName());
            seat.setSeatType(seatDTO.getType());
            seat.setCreatedAt(LocalDateTime.now());
            seat.setCreatedBy(userReq);
            seat.setUpdatedAt(null);
            Seat seat1 = seatRepository.save(seat);
            DetailResponse<Seat> response = mappingService.mapToResponse(seat1); // new DetailResponse<>(seat1, seat1.getCreatedBy(),null);
            responses.add(response);
        }

        return responses;
    }

    public Page<DetailResponse<Seat>> getByDeleted(boolean b, int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Seat> seats = seatRepository.findAllByDeleted(b, pageable);
        return mappingService.mapToResponse(seats);
    }

    public DetailResponse<Seat> getById(Long id) {
        Seat seat = seatRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(seat);
    }

    public Seat create(HttpServletRequest request, Seat seat) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        seat.setId(null);
        seat.setCreatedBy(userReq);
        seat.setCreatedAt(LocalDateTime.now());
        seat.setUpdatedAt(null);
        return seatRepository.save(seat);
    }

    public List<DetailResponse<Seat>> createSeatsByRoom(HttpServletRequest request, Long roomId, List<SeatDTO> seatDTOS) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Room room = roomRepository.findById(roomId).orElseThrow();
        return createSeats(room, seatDTOS, userReq);
    }

    public DetailResponse<Seat> update(HttpServletRequest request, Long id, Seat seat) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Seat seat1 = seatRepository.findById(id).orElseThrow();
        seat1.setRow(seat.getRow());
        seat1.setColumn(seat.getColumn());
        seat1.setRoom(seat.getRoom());
        seat1.setSeatType(seat.getSeatType());
        seat1.setName(seat.getName());
        seat1.setUpdatedAt(LocalDateTime.now());
        seat1.setUpdatedBy(userReq);
        Seat newSeat = seatRepository.save(seat1);
        return mappingService.mapToResponse(newSeat);
    }

    public void delete(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Seat seat = seatRepository.findById(id).orElseThrow();
        seat.setDeleted(true);
        seat.setUpdatedBy(userReq);
        seat.setUpdatedAt(LocalDateTime.now());
        seatRepository.save(seat);
    }

    public DetailResponse<Seat> restore(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Seat seat = seatRepository.findById(id).orElseThrow();
        seat.setDeleted(true);
        seat.setUpdatedBy(userReq);
        seat.setUpdatedAt(LocalDateTime.now());
        Seat seat1 = seatRepository.save(seat);
        return mappingService.mapToResponse(seat1);
    }
}
