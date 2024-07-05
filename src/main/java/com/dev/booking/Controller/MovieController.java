package com.dev.booking.Controller;

import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.MovieResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/movie")
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Movie>>>> getAll() {
        List<Movie> movies = movieRepository.findAll();

        List<DetailResponse<Movie>> result = movies.stream().map(movie -> {
            UserBasicDTO createdBy = null;
            if (movie.getCreatedBy() != null) {
                User user = userRepository.findById(movie.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (movie.getUpdatedBy() != null) {
                User user = userRepository.findById(movie.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(movie, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));

    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieResponse>>> getById(@PathVariable Long id) {
        if (movieRepository.existsById(id)) {
            UserBasicDTO createdBy = null;
            UserBasicDTO updatedBy = null;

            MovieResponse movie = movieService.getById(id);
            if(movie.getMovie().getCreatedBy() != null){
                User user = userRepository.findById(movie.getMovie().getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            if(movie.getMovie().getUpdatedBy() != null){
                User user = userRepository.findById(movie.getMovie().getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            DetailResponse<MovieResponse> response = new DetailResponse<>(movie,createdBy,updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Movie>>> create(@RequestParam("name") String name,
                                                        @RequestParam("releaseDate")  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate,
                                                        @RequestParam("overview") String overview,
                                                        @RequestParam("duration") int duration,
                                                        @RequestParam("image") MultipartFile image,
                                                        @RequestParam("trailer") String trailer,
                                                        HttpServletRequest request) {
//        Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
//        String username = (String) tokenAndUsername.get("username");
//        User userReq = userRepository.findByUserName(username).orElse(null);
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        try {
            Movie movie = new Movie();
            movie.setName(name);
            movie.setReleaseDate(releaseDate);
            movie.setOverview(overview);
            movie.setDuration(duration);
            movie.setImage(image.getBytes());
            movie.setTrailer(trailer);
            movie.setCreatedAt(LocalDateTime.now());
            movie.setCreatedBy(userReq.getId());
            movie.setUpdatedAt(null);
            Movie newMovie = movieRepository.save(movie);

            UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<Movie> response = new DetailResponse<>(newMovie, createdBy, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject<>("Could not save movie", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Movie>>> update(@PathVariable Long id,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("releaseDate")  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate,
                                                 @RequestParam("overview") String overview,
                                                 @RequestParam("duration") int duration,
                                                 @RequestParam("image") MultipartFile image,
                                                 @RequestParam("trailer") String trailer,
                                                        HttpServletRequest request) {
        if (movieRepository.existsById(id)) {
            try {
//                Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
//                String username = (String) tokenAndUsername.get("username");
//                User userReq = userRepository.findByUserName(username).orElse(null);
                User userReq = jwtRequestFilter.getUserRequest(request);
                if(userReq == null){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
                }
                Movie movie = movieRepository.findById(id).orElse(null);
                movie.setId(id);
                movie.setName(name);
                movie.setReleaseDate(releaseDate);
                movie.setOverview(overview);
                movie.setDuration(duration);
                movie.setImage(image.getBytes());
                movie.setTrailer(trailer);
                movie.setUpdatedAt(LocalDateTime.now());
                movie.setUpdatedBy(userReq.getId());
                Movie newMovie = movieRepository.save(movie);
                User user = userRepository.findById(newMovie.getCreatedBy()).orElse(null);
                UserBasicDTO createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
                UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
                DetailResponse<Movie> response = new DetailResponse<>(newMovie, createdBy, updatedBy);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject<>("Could not update movie",null));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Movie>> delete(@PathVariable Long id){
        if(movieRepository.existsById(id)){
            movieRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Movie>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Movie>("id does not exist",null));

    }

}
