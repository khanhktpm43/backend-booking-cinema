package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.MovieResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.MovieService;
import com.dev.booking.Service.ShowtimeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/movies")
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Movie>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Movie>> result = movieService.getAllByDeleted(false, page, size, sort, name);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }

    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Movie>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Movie>> result = movieService.getAllByDeleted(true, page, size, sort, name);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));
    }
    @GetMapping("/current")
    public ResponseEntity<ResponseObject<List<DetailResponse<Movie>>>> getCurrentMovies(){
        List<DetailResponse<Movie>> responses = movieService.getMoviesWithActiveShowtimes();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseObject<List<DetailResponse<Movie>>>> getUpcomingMovies(){
        List<DetailResponse<Movie>> responses = movieService.getMoviesUpcoming();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<ResponseObject<Map<LocalDate, List<Showtime>>>> getShowtime(@PathVariable Long id){
        Map<LocalDate, List<Showtime>> response = showtimeService.getShowtimesByMovie(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieResponse>>> getById(@PathVariable Long id) {
        if (movieRepository.existsById(id)) {
            MovieResponse movie = movieService.getById(id);
            DetailResponse<MovieResponse> response = new DetailResponse<>(movie, movie.getMovie().getCreatedBy(), movie.getMovie().getUpdatedBy(), movie.getMovie().getCreatedAt(), movie.getMovie().getUpdatedAt());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Movie>>> create(@RequestParam("name") String name, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate, @RequestParam("overview") String overview, @RequestParam("duration") int duration, @RequestParam("image") MultipartFile image, @RequestParam("trailer") String trailer, HttpServletRequest request) {
        DetailResponse<Movie> response = movieService.create(request, name, releaseDate, overview, duration, image, trailer);
        if (response != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject<>("Could not save movie", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Movie>>> update(@PathVariable Long id, @RequestParam("name") String name, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate, @RequestParam("overview") String overview, @RequestParam("duration") int duration, @RequestParam("image") MultipartFile image, @RequestParam("trailer") String trailer, HttpServletRequest request) throws IOException {
        if (movieRepository.existsById(id)) {
            DetailResponse<Movie> response = movieService.update(id, request, name, releaseDate, overview, duration, image, trailer);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Movie>> softDelete(@PathVariable Long id, HttpServletRequest request) {
        if (movieRepository.existsByIdAndDeleted(id, false)) {
            movieService.delete(request, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject<Movie>> restore(@PathVariable Long id, HttpServletRequest request) {
        if (!movieRepository.existsByIdAndDeleted(id, true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        Movie movie = movieService.restore(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", movie));
    }

}
