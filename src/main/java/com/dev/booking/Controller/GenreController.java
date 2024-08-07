package com.dev.booking.Controller;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.GenreRepository;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.GenreService;
import com.dev.booking.Service.MappingService;
import io.swagger.v3.oas.models.examples.Example;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/genres")
@CrossOrigin(origins = "*")
public class GenreController {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private GenreService genreService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Genre>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Genre>> responses = genreService.getAll(page, size, sort, name);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> getById(@PathVariable Long id) {
        if (!genreRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        DetailResponse<Genre> response = genreService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> create(@RequestBody Genre genre, HttpServletRequest request) {
        if (genreRepository.existsByName(genre.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("genre name does  exist", null));
        }
        DetailResponse<Genre> response = genreService.create(request, genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> update(@PathVariable Long id, @RequestBody Genre genre, HttpServletRequest request) {
        if (genreRepository.existsById(id)) {
            DetailResponse<Genre> response = genreService.update(request, id , genre);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> delete(@PathVariable Long id) {
        if (genreRepository.existsById(id)) {
            genreService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}
