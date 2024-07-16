package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieCast;
import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.MovieCastDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.MovieCastService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie-casts")
public class MovieCastController {
    @Autowired
    private MovieCastRepository movieCastRepository;
    @Autowired
    private MovieCastService movieCastService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MappingService mappingService;


    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<MovieCast>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<MovieCast> movieCasts = movieCastRepository.findAll(pageable);
        Page<DetailResponse<MovieCast>> responses =mappingService.mapToResponse(movieCasts);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @PostMapping("/movie")
    public ResponseEntity<ResponseObject<List<DetailResponse<MovieCast>>>> getByMovie(@RequestBody Movie movie){
        if(movie.getId() == null || !movieRepository.existsById(movie.getId()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("movieId does not exist",null));
        List<MovieCast> movieCasts = movieCastRepository.findByMovie(movie);
        List<DetailResponse<MovieCast>> responses = movieCastService.mapMovieCastToResponse(movieCasts);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieCast>>> getById(@PathVariable Long id){
        if(movieCastRepository.existsById(id)){
            MovieCast movieCast = movieCastRepository.findById(id).orElse(null);
            DetailResponse<MovieCast> response = mappingService.mapToResponse(movieCast);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PostMapping("")
    public  ResponseEntity<ResponseObject<DetailResponse<Movie>>> create(@RequestBody MovieCastDTO movieCastDTO, HttpServletRequest request){

        DetailResponse<Movie> response = movieCastService.attachCasts(request,movieCastDTO);
        if(response == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("movie does not exist or genres is empty or not authenticated or roleCast invalid",null));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",response));
    }
    @PutMapping("/{id}")
    public  ResponseEntity<ResponseObject<DetailResponse<MovieCast>>> update(@PathVariable Long id, @RequestBody MovieCast movieCast, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("not authenticated",null));
        if(!movieCastRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        DetailResponse<MovieCast> response= movieCastService.update(userReq,id,movieCast);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieCast>> delete(@PathVariable Long id){
        if(movieCastRepository.existsById(id)){
            movieCastRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null) );
    }
    @DeleteMapping("/movie")
    public ResponseEntity<ResponseObject<MovieGenre>> deleteByMovie(@RequestBody Movie movie){
        if(movieRepository.existsById(movie.getId())){
            movieCastRepository.deleteByMovie(movie);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieGenre>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieGenre>("movie does not exist",null) );
    }
}
