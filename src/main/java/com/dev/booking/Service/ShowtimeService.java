package com.dev.booking.Service;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public List<DetailResponse<Showtime>> mapToResponse(List<Showtime> showtimes) {
        return showtimes.stream().map(showtime -> {
            UserBasicDTO createdBy = null;
            if (showtime.getCreatedBy() != null) {
                User user = userRepository.findById(showtime.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (showtime.getUpdatedBy() != null) {
                User user = userRepository.findById(showtime.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(showtime, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }
    public DetailResponse<Showtime> getById(Long id){
        Showtime showtime = showtimeRepository.findById(id).orElse(null);
        UserBasicDTO createdBy = null;
        if(showtime != null && showtime.getCreatedBy() != null){
            User user = userRepository.findById(showtime.getCreatedBy()).orElse(null);
            if (user != null) {
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }
        UserBasicDTO updatedBy = null;
        if(showtime != null && showtime.getUpdatedBy() != null){
            User user = userRepository.findById(showtime.getUpdatedBy()).orElse(null);
            if (user != null) {
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }
        return new DetailResponse<>(showtime, createdBy, updatedBy);
    }
    public boolean isValid(Showtime showtime){
        return showtimeRepository.isValid(showtime);

    }
}
