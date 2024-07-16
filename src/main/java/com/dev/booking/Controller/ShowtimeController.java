package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.ShowtimeResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.MappingService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/showtimes")
public class ShowtimeController {
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MappingService mappingService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Showtime>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<Showtime> showtimes = showtimeRepository.findByDeleted(false, pageable);
        Page<DetailResponse<Showtime>> responses = mappingService.mapToResponse(showtimes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Showtime>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Showtime> showtimes = showtimeRepository.findByDeleted(true, pageable);
        Page<DetailResponse<Showtime>> responses = mappingService.mapToResponse(showtimes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>>  getById(@PathVariable Long id){
        if(showtimeRepository.existsById(id)){
            DetailResponse<Showtime> response = showtimeService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @GetMapping("/date")
    public ResponseEntity<ResponseObject<List<ShowtimeResponse>>>  getById( @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<ShowtimeResponse> responses = showtimeService.getShowtimesByDate(date);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> create(@RequestBody Showtime showtime, HttpServletRequest request){
        if(showtimeRepository.isValid(showtime)){
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            if(movieRepository.existsByIdAndDeleted(showtime.getMovie().getId(),false))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("movie has been deleted", null));
            showtime.setId(null);
            showtime.setCreatedBy(userReq);
            showtime.setCreatedAt(LocalDateTime.now());
            showtime.setUpdatedAt(null);
            Showtime showtime1 = showtimeRepository.save(showtime);
            DetailResponse<Showtime> response = new DetailResponse<>(showtime1, showtime1.getCreatedBy(), null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("invalid", null));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> update(@PathVariable Long id,@RequestBody Showtime showtime, HttpServletRequest request){
        if(showtimeRepository.existsById(id)){
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            if(showtimeRepository.checkDuplicate(showtime,id))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
            Showtime showtime1 = showtimeRepository.findById(id).orElse(null);
            if(showtime1 != null){
                showtime.setUpdatedAt(LocalDateTime.now());
                showtime.setUpdatedBy(userReq);
                showtime.setCreatedAt(showtime1.getCreatedAt());
                showtime.setCreatedBy(showtime1.getCreatedBy());
                Showtime showtime2 =  showtimeRepository.save(showtime);
                DetailResponse<Showtime> response = new DetailResponse<>(showtime2, showtime2.getCreatedBy(), showtime2.getUpdatedBy());
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("id does not exist or dayType invalid", null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> delete(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if(showtimeRepository.existsByIdAndDeleted(id,false)){
            Showtime showtime = showtimeRepository.findById(id).orElse(null);
            if(showtime != null && LocalDateTime.now().isAfter(showtime.getStartTime()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Cannot delete expired showtime", null));
            showtime.setDeleted(true);
            showtime.setUpdatedAt(LocalDateTime.now());
            showtime.setUpdatedBy(userReq);
            showtimeRepository.save(showtime);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<Showtime>> restore(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        Showtime showtime = showtimeRepository.findByIdAndDeleted(id, true).orElse(null);
        if (showtime == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        showtime.setDeleted(false);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtime.setUpdatedBy(userReq);
        showtimeRepository.save(showtime);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",showtime));
    }
}
