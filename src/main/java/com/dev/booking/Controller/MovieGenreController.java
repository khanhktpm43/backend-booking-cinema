package com.dev.booking.Controller;

import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.ResponseDTO.ResponseObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/movie-genre")
public class MovieGenreController {
    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<MovieGenre>>> getAll(){
        List<MovieGenre> movieGenres = movieGenreRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<MovieGenre>>("",movieGenres));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieGenre>> getById(@PathVariable Long id){
        if(movieGenreRepository.existsById(id)){
            MovieGenre movieGenre = movieGenreRepository.findById(id).orElse(null);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieGenre>("",movieGenre));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    @PostMapping("")
    public  ResponseEntity<ResponseObject<MovieGenre>> create(@RequestBody MovieGenre movieGenre){

        Example<MovieGenre> example = Example.of(movieGenre);
        if(!movieGenreRepository.exists(example)){
            movieGenre.setId(null);
            MovieGenre newMovieGenre = movieGenreRepository.save(movieGenre);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<MovieGenre>("",newMovieGenre));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<MovieGenre>("movieGenre does exist",null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieGenre>> delete(@PathVariable Long id){
        if(movieGenreRepository.existsById(id)){
            movieGenreRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieGenre>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieGenre>("id does not exist",null) );
    }
}
