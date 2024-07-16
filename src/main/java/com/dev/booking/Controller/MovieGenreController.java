package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.MovieGenreDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;

import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.MovieGenreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movie-genres")
public class MovieGenreController {
    @Autowired
    private MovieGenreRepository movieGenreRepository;
    @Autowired
    private MovieGenreService movieGenreService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MappingService mappingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<MovieGenre>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<MovieGenre> movieGenres = movieGenreRepository.findAll(pageable);
        Page<DetailResponse<MovieGenre>> responses = mappingService.mapToResponse(movieGenres);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
//    @GetMapping("/movie")
//    public ResponseEntity<ResponseObject<List<DetailResponse<MovieGenre>>>> getAll(@RequestBody Movie movie){
//        if(movie.getId() == null || !movieRepository.existsById(movie.getId()))
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("movieId does not exist",null));
//        List<MovieGenre> movieGenres = movieGenreRepository.findByMovie(movie);
//        List<DetailResponse<MovieGenre>> responses = mappingService.mapToResponse(movieGenres);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieGenre>>> getById(@PathVariable Long id){
        if(movieGenreRepository.existsById(id)){
            MovieGenre movieGenre = movieGenreRepository.findById(id).orElse(null);
            DetailResponse<MovieGenre> response = new DetailResponse<>(movieGenre, movieGenre.getCreatedBy(), movieGenre.getUpdatedBy());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PostMapping("")
    public  ResponseEntity<ResponseObject<DetailResponse<Movie>>> create(@RequestBody MovieGenreDTO movieGenreDTO, HttpServletRequest request){
        DetailResponse<Movie> response = movieGenreService.attachGenres(request,movieGenreDTO);
        if(response == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("movie does not exist or genres is empty or not authenticated",null));

       return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",response));
    }
    @PutMapping("/{id}")
    public  ResponseEntity<ResponseObject<DetailResponse<MovieGenre>>> update(@PathVariable Long id, @RequestBody MovieGenre movieGenre, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("not authenticated",null));
        if(!movieGenreRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        DetailResponse<MovieGenre> response= movieGenreService.update(userReq,id,movieGenre);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieGenre>> delete(@PathVariable Long id){
        if(movieGenreRepository.existsById(id)){
            movieGenreRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieGenre>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieGenre>("id does not exist",null) );
    }
    @DeleteMapping("/movie")
    public ResponseEntity<ResponseObject<MovieGenre>> deleteByMovie(@RequestBody Movie movie){
        if(movieRepository.existsById(movie.getId())){
            movieGenreRepository.deleteByMovie(movie);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieGenre>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieGenre>("movie does not exist",null) );
    }
}
