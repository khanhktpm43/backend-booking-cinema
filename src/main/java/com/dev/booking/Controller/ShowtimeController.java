package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.ShowtimeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.ShowtimeResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.ShowtimeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/showtime")
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

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Showtime>>>> getAll(){
        List<Showtime> showtimes = showtimeRepository.findByDeleted(false);
        List<DetailResponse<Showtime>> responses = showtimeService.mapToResponse(showtimes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",responses));
    }
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<List<DetailResponse<Showtime>>>> getAllByDeleted(){
        List<Showtime> showtimes = showtimeRepository.findByDeleted(true);
        List<DetailResponse<Showtime>> responses = showtimeService.mapToResponse(showtimes);
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
            showtime.setCreatedBy(userReq.getId());
            showtime.setCreatedAt(LocalDateTime.now());
            showtime.setUpdatedAt(null);
            Showtime showtime1 = showtimeRepository.save(showtime);
            UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<Showtime> response = new DetailResponse<>(showtime1, createdBy, null);
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
                showtime.setUpdatedBy(userReq.getId());
                showtime.setCreatedAt(showtime1.getCreatedAt());
                showtime.setCreatedBy(showtime1.getCreatedBy());
                Showtime showtime2 =  showtimeRepository.save(showtime);
                UserBasicDTO createdBy = null;
                User user = userRepository.findById(showtime2.getCreatedBy()).orElse(null);
                if(user != null)
                    createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
                UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
                DetailResponse<Showtime> response = new DetailResponse<>(showtime2, createdBy, updatedBy);
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
            showtime.setUpdatedBy(userReq.getId());
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
        showtime.setUpdatedBy(userReq.getId());
        showtimeRepository.save(showtime);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",showtime));
    }
}
