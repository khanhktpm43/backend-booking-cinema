package com.dev.booking.Service;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.SeatDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private MappingService mappingService;
    public List<DetailResponse<Seat>> createSeats(Room room, List<SeatDTO> seatDTOS, User userReq) {
        List<DetailResponse<Seat>> responses = new ArrayList<>();
        for(SeatDTO seatDTO : seatDTOS){
            Seat seat = new Seat();
            seat.setRow(seatDTO.getRow());
            seat.setRoom(room);
            seat.setColumn(seatDTO.getColumn());
            if(seatRepository.existsByRoomAndRowAndColumnAndDeleted(room,seatDTO.getRow(),seatDTO.getColumn(),false)) {
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
            DetailResponse<Seat> response = new DetailResponse<>(seat1, seat1.getCreatedBy(),null);
            responses.add(response);
        }

        return responses;
    }
}
