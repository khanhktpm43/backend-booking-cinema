package com.dev.booking.Controller;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Repository.GenreRepository;
import com.dev.booking.ResponseDTO.ResponseObject;
import io.swagger.v3.oas.models.examples.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/genre")

public class GenreController {
    @Autowired
    private GenreRepository genreRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<Genre>>> getAll(){
        List<Genre> genres = genreRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<Genre>>("",genres));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<Genre>> getById(@PathVariable Long id){

            Genre genre = genreRepository.findById(id).orElse(null);
       if(genre != null){
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Genre>("",genre));
       }
       return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Genre>("id does not exist",genre));
    }

    @PostMapping("")
    public  ResponseEntity<ResponseObject<Genre>> create(@RequestBody Genre genre){

        if(genreRepository.existsByName(genre.getName())){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<Genre>("genre name does  exist",genre));
        }
        Genre newGenre = genreRepository.save(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<Genre>("",newGenre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Genre>> update(@PathVariable Long id, @RequestBody Genre genre){
        if(genreRepository.existsById(id)){
            genre.setId(id);
            genreRepository.save(genre);
            return ResponseEntity.status(HttpStatus.OK ).body(new ResponseObject<Genre>("",genre));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Genre>("id does not exist",null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Genre>> delete(@PathVariable Long id){
        if(genreRepository.existsById(id)){
            genreRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK ).body(new ResponseObject<Genre>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Genre>("id does not exist",null));
    }
}
