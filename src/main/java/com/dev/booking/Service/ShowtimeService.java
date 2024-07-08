package com.dev.booking.Service;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieService movieService;

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

    public List<ShowtimeResponse> getShowtimesByDate(LocalDate date) {
        List<Object[]> results = showtimeRepository.findShowtimesByDate(date);
        List<ShowtimeResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            ShowtimeResponse response = new ShowtimeResponse();
            Long movieId = (Long) result[0];
            MovieResponse movie = movieService.getById(movieId);
          //  response.setMovies(movie);
            response.setMovie(movie.getMovie());
            response.setCasts(movie.getCasts());
            response.setGenres(movie.getGenres());
            String showtimes = (String) result[1] ;
            List<ShowtimeDTO> showtimeDtos = parseShowtimeList(showtimes);
            response.setShowtimes(showtimeDtos);
            responses.add(response);
        }
        return responses;
    }
    private List<ShowtimeDTO> parseShowtimeList(String listColumn) {
        List<ShowtimeDTO> showtimeDtos = new ArrayList<>();
        String[] showtimes = listColumn.split("\\|");

        for (String showtime : showtimes) {
            String[] parts = showtime.split("-");
            Long id = Long.parseLong(parts[0]);
            String time = parts[1];

            showtimeDtos.add(new ShowtimeDTO(id, time));
        }

        return showtimeDtos;
    }
}
