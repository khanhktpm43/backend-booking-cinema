package com.dev.booking.Service;
import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoomRepository;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private MovieService movieService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private RoomRepository roomRepository;

    public DetailResponse<Showtime> getById(Long id){
        Showtime showtime = showtimeRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(showtime);
    }

    public List<ShowtimeResponse> getShowtimesByDate(LocalDate date) {
        List<Object[]> results = showtimeRepository.findShowtimesByDate(date);
        List<ShowtimeResponse> responses = new ArrayList<>();
        for (Object[] result : results) {
            ShowtimeResponse response = new ShowtimeResponse();
            Long movieId = (Long) result[0];
            MovieResponse movie = movieService.getById(movieId);
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

    public Page<DetailResponse<Showtime>> getByDeleted(boolean b, int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Showtime> showtimes = showtimeRepository.findByDeleted(false, pageable);
        return mappingService.mapToResponse(showtimes);
    }

    public List<ShowtimeSeat> getSeatsByShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id).orElseThrow();
        return seatRepository.findByShowtime(showtime);
    }

    public DetailResponse<Showtime> create(HttpServletRequest request, Showtime showtime) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Room room = roomRepository.findById(showtime.getRoom().getId()).orElse(null);
        showtime.setId(null);
        showtime.setRoom(room);
        showtime.setCreatedBy(userReq);
        showtime.setCreatedAt(LocalDateTime.now());
        showtime.setUpdatedAt(null);
        Showtime showtime1 = showtimeRepository.save(showtime);
        return mappingService.mapToResponse(showtime1);
    }

    public DetailResponse<Showtime> update(HttpServletRequest request, Long id, Showtime showtime) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Showtime showtime1 = showtimeRepository.findById(id).orElseThrow();
            showtime.setUpdatedAt(LocalDateTime.now());
            showtime.setUpdatedBy(userReq);
            showtime.setCreatedAt(showtime1.getCreatedAt());
            showtime.setCreatedBy(showtime1.getCreatedBy());
            Showtime showtime2 =  showtimeRepository.save(showtime);
           return mappingService.mapToResponse(showtime2);
    }

    public void delete(HttpServletRequest request, Showtime showtime) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        showtime.setDeleted(true);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtime.setUpdatedBy(userReq);
        showtimeRepository.save(showtime);
    }

    public DetailResponse<Showtime> restore(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Showtime showtime = showtimeRepository.findByIdAndDeleted(id, true).orElseThrow();
        showtime.setDeleted(false);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtime.setUpdatedBy(userReq);
        Showtime showtime1= showtimeRepository.save(showtime);
        return mappingService.mapToResponse(showtime1);
    }
}
