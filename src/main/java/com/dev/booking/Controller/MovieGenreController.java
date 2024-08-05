package com.dev.booking.Controller;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.GenreRepository;
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
@CrossOrigin(origins = "*")
public class MovieGenreController {
    @Autowired
    private MovieGenreRepository movieGenreRepository;
    @Autowired
    private MovieGenreService movieGenreService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<MovieGenre>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<MovieGenre>> responses = movieGenreService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieGenre>>> getById(@PathVariable Long id){
        if(movieGenreRepository.existsById(id)){
            DetailResponse<MovieGenre> response =  movieGenreService.getById(id);
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
    public  ResponseEntity<ResponseObject<DetailResponse<MovieGenre>>> update(@PathVariable Long id, @RequestBody Genre genre, HttpServletRequest request){
        if(!movieGenreRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        if(!genreRepository.existsById(genre.getId()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("genre does not exist",null));
        DetailResponse<MovieGenre> response= movieGenreService.update(request,id,genre);
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
