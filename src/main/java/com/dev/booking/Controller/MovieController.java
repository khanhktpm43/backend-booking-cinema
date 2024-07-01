package com.dev.booking.Controller;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.ResponseDTO.MovieResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.MovieService;
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

@RestController
@RequestMapping("api/v1/movie")
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<Movie>>> getAll() {
        List<Movie> movies = movieRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<Movie>>("", movies));

    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseObject<MovieResponse>> getById(@PathVariable Long id) {
        if (movieRepository.existsById(id)) {
            MovieResponse movie = movieService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieResponse>("", movie));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieResponse>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<Movie>> create(@RequestParam("name") String name,
                                                 @RequestParam("releaseDate")  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate,
                                                 @RequestParam("overview") String overview,
                                                 @RequestParam("duration") int duration,
                                                 @RequestParam("image") MultipartFile image,
                                                 @RequestParam("trailer") MultipartFile trailer) {
        try {
            Movie movie = new Movie();
            movie.setName(name);
            movie.setReleaseDate(releaseDate);
            movie.setOverview(overview);
            movie.setDuration(duration);
            movie.setImage(image.getBytes());
            movie.setTrailer(trailer.getBytes());
            Movie newMovie = movieRepository.save(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<Movie>("", newMovie));

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject<Movie>("Could not save movie", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Movie>> update(@PathVariable Long id,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("releaseDate")  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime releaseDate,
                                                 @RequestParam("overview") String overview,
                                                 @RequestParam("duration") int duration,
                                                 @RequestParam("image") MultipartFile image,
                                                 @RequestParam("trailer") MultipartFile trailer) {
        if (movieRepository.existsById(id)) {
            try {
                Movie movie = movieRepository.findById(id).orElse(null);
                movie.setId(id);
                movie.setName(name);
                movie.setReleaseDate(releaseDate);
                movie.setOverview(overview);
                movie.setDuration(duration);
                movie.setImage(image.getBytes());
                movie.setTrailer(trailer.getBytes());
                Movie newMovie = movieRepository.save(movie);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Movie>("", newMovie));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject<Movie>("Could not update movie",null));
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Movie>("id does not exist",null));
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
