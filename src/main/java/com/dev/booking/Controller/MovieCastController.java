package com.dev.booking.Controller;

import com.dev.booking.Entity.MovieCast;
import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.ResponseDTO.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie-cast")
public class MovieCastController {
    @Autowired
    private MovieCastRepository movieCastRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<MovieCast>>> getAll(){
        List<MovieCast> movieCasts = movieCastRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<MovieCast>>("",movieCasts));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieCast>> getById(@PathVariable Long id){
        if(movieCastRepository.existsById(id)){
            MovieCast movieCast = movieCastRepository.findById(id).orElse(null);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieCast>("",movieCast));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    @PostMapping("")
    public  ResponseEntity<ResponseObject<MovieCast>> create(@RequestBody MovieCast movieCast){

        Example<MovieCast> example = Example.of(movieCast);
        if(!movieCastRepository.exists(example) && (movieCast.getRoleCast() == 1 || movieCast.getRoleCast() == 2 || movieCast.getRoleCast() == 3)){
            movieCast.setId(null);
            MovieCast newMovieCast= movieCastRepository.save(movieCast);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<MovieCast>("",newMovieCast));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<MovieCast>("movieCast does exist",null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<MovieCast>> delete(@PathVariable Long id){
        if(movieCastRepository.existsById(id)){
            movieCastRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<MovieCast>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<MovieCast>("id does not exist",null) );
    }
}
