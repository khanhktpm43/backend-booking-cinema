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
import com.dev.booking.RequestDTO.CreateShowtimeRequest;
import com.dev.booking.RequestDTO.TimeRange;
import com.dev.booking.ResponseDTO.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private BookingService bookingService;

    public DetailResponse<Showtime> getById(Long id) {
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
            String showtimes = (String) result[1];
            List<ShowtimeDTO> showtimeDtos = parseShowtimeList(showtimes);
            response.setShowtimes(showtimeDtos);
            responses.add(response);
        }
        return responses;
    }

    public Map<LocalDate, List<Showtime>> getShowtimesByMovie(Long movieId) {
        List<Showtime> showtimes = showtimeRepository.findShowtimesByMovieAndStartTime(movieId);
        return showtimes.stream()
                .collect(Collectors.groupingBy(
                        showtime -> showtime.getStartTime().toLocalDate()
                ));
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
        Page<Showtime> showtimes = showtimeRepository.findByDeleted(b, pageable);
        return mappingService.mapToResponse(showtimes);
    }

    public List<ShowtimeSeat> getSeatsByShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id).orElseThrow();
        return seatRepository.findByShowtime(showtime, showtime.getRoom());
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
        showtime.setRoom(roomRepository.findById(showtime.getRoom().getId()).orElseThrow());
        showtime.setUpdatedAt(LocalDateTime.now());
        showtime.setUpdatedBy(userReq);
        showtime.setCreatedAt(showtime1.getCreatedAt());
        showtime.setCreatedBy(showtime1.getCreatedBy());
        Showtime showtime2 = showtimeRepository.save(showtime);
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
        Showtime showtime1 = showtimeRepository.save(showtime);
        return mappingService.mapToResponse(showtime1);
    }

    public List<DetailResponse<Showtime>> createShowtimes(CreateShowtimeRequest createShowtime, HttpServletRequest request) {
        User user = jwtRequestFilter.getUserRequest(request);
        List<Showtime> showtimes = new ArrayList<>();
        LocalDate currentDate = createShowtime.getStartDate();
        while (!currentDate.isAfter(createShowtime.getEndDate())) {
            for (TimeRange time : createShowtime.getTimes()) {
                Showtime showtime = new Showtime();
                showtime.setCreatedAt(LocalDateTime.now());
                showtime.setUpdatedAt(null);
                showtime.setCreatedBy(user);
                showtime.setMovie(createShowtime.getMovie());
                showtime.setRoom(roomRepository.findById(createShowtime.getRoom().getId()).get());
                showtime.setStartTime(currentDate.atTime(time.getStartTime()));
                showtime.setEndTime(currentDate.atTime(time.getEndTime()));
                showtime.setDeleted(false);
                if (showtimeRepository.isValid(showtime)) {
                    showtimes.add(showtime);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        return mappingService.mapToResponse(showtimeRepository.saveAll(showtimes));
    }
}
