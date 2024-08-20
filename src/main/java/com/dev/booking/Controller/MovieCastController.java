package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieCast;
import com.dev.booking.Entity.MovieGenre;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.RequestDTO.CastReq;
import com.dev.booking.RequestDTO.MovieCastDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.MovieCastService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movie-casts")
@CrossOrigin(origins = "*")
public class MovieCastController {
    @Autowired
    private MovieCastRepository movieCastRepository;
    @Autowired
    private MovieCastService movieCastService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CastRepository castRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<MovieCast>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<MovieCast>> responses = movieCastService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<MovieCast>>> getById(@PathVariable Long id){
        if(movieCastRepository.existsById(id)){
            DetailResponse<MovieCast> response = movieCastService.getById(id);
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
    public  ResponseEntity<ResponseObject<DetailResponse<MovieCast>>> update(@PathVariable Long id, @RequestBody CastReq castDTO, HttpServletRequest request){
        if(!movieCastRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        if(!castRepository.existsById(castDTO.getCast().getId()) || (castDTO.getRoleCast() != 1 && castDTO.getRoleCast() != 0))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("cast does not exist or role invalid",null));
        DetailResponse<MovieCast> response= movieCastService.update(request,id,castDTO);
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
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null) );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("movie does not exist",null) );
    }
}
