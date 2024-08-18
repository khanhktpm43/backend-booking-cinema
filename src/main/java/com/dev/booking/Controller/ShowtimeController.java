package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.*;
import com.dev.booking.RequestDTO.CreateShowtimeRequest;
import com.dev.booking.ResponseDTO.*;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.SeatPriceService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private SeatPriceService seatPriceService;
    @Autowired
    private ShowtimeService     showtimeService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private RoomRepository roomRepository;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Showtime>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Showtime>> responses = showtimeService.getByDeleted(false, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Showtime>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<DetailResponse<Showtime>> responses = showtimeService.getByDeleted(true, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> getById(@PathVariable Long id) {
        if (showtimeRepository.existsById(id)) {
            DetailResponse<Showtime> response = showtimeService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @GetMapping("/{id}/prices")
    public ResponseEntity<ResponseObject<List<DetailResponse<SeatPrice>>>> getPricingTableByShowtime(@PathVariable Long id) {
        if (!showtimeRepository.existsByIdAndDeleted(id, false)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        List<DetailResponse<SeatPrice>> responses = seatPriceService.getPricesByShowtime(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ResponseObject<List<ShowtimeSeat>>> getSeatByShowtime(@PathVariable Long id) {
        if (!showtimeRepository.existsByIdAndDeleted(id, false)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        List<ShowtimeSeat> seats = showtimeService.getSeatsByShowtime(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", seats));
    }

    @GetMapping("/date")
    public ResponseEntity<ResponseObject<List<ShowtimeResponse>>> getById(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ShowtimeResponse> responses = showtimeService.getShowtimesByDate(date);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

//    @PostMapping("")
//    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> create(@RequestBody Showtime showtime, HttpServletRequest request) {
//        if (showtimeRepository.isValid(showtime)) {
//            if (movieRepository.existsByIdAndDeleted(showtime.getMovie().getId(), true))
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("movie has been deleted", null));
//            if (!roomRepository.existsById(showtime.getRoom().getId())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("id room does not exist", null));
//            }
//            DetailResponse<Showtime> response = showtimeService.create(request, showtime); // new DetailResponse<>(showtime1, showtime1.getCreatedBy(), null);
//            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("invalid", null));
//    }
    @PreAuthorize("hasRole('EMPLOYEE')")
   @PostMapping("")
   public ResponseEntity<ResponseObject<List<DetailResponse<Showtime>>>> create(@RequestBody CreateShowtimeRequest createShowtimeRequest, HttpServletRequest request){
       if (movieRepository.existsByIdAndDeleted(createShowtimeRequest.getMovie().getId(), true)) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("movie has been deleted", null));
       }
       if (!roomRepository.existsById(createShowtimeRequest.getRoom().getId())) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("id room does not exist", null));
       }
       List<DetailResponse<Showtime>> responses = showtimeService.createShowtimes(createShowtimeRequest, request);
       return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", responses));
   }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> update(@PathVariable Long id, @RequestBody Showtime showtime, HttpServletRequest request) {
        if (showtimeRepository.existsById(id)) {
            if (showtimeRepository.checkDuplicate(showtime, id))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
            DetailResponse<Showtime> response = showtimeService.update(request, id, showtime); // new DetailResponse<>(showtime2, showtime2.getCreatedBy(), showtime2.getUpdatedBy());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("id does not exist or dayType invalid", null));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> delete(@PathVariable Long id, HttpServletRequest request) {
        if (showtimeRepository.existsByIdAndDeleted(id, false)) {
            Showtime showtime = showtimeRepository.findById(id).orElseThrow();
            if (LocalDateTime.now().isAfter(showtime.getStartTime()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Cannot delete expired showtime", null));
            showtimeService.delete(request, showtime);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Showtime>>> restore(@PathVariable Long id, HttpServletRequest request) {
        if (!showtimeRepository.existsByIdAndDeleted(id, true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        }
        DetailResponse<Showtime> response = showtimeService.restore(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }

}
