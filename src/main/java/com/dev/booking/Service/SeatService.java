package com.dev.booking.Service;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public List<DetailResponse<Seat>> mapSeatToSeatResponse(List<Seat> seats){
        List<DetailResponse<Seat>> result = seats.stream().map(seat -> {
            UserBasicDTO createdBy = null;
            if (seat.getCreatedBy() != null) {
                User user = userRepository.findById(seat.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (seat.getUpdatedBy() != null) {
                User user = userRepository.findById(seat.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(seat, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return result;
    }
}
