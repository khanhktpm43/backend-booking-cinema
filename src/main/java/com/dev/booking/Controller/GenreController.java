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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/genres")

public class GenreController {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieGenreRepository movieGenreRepository;
    @Autowired
    private GenreService genreService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Genre>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Genre> genres = genreRepository.findAll(pageable);
        Page<DetailResponse<Genre>> responses = mappingService.mapToResponse(genres);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> getById(@PathVariable Long id) {
        if (!genreRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        Genre genre = genreRepository.findById(id).orElse(null);
        DetailResponse<Genre> response = mappingService.mapToResponse(genre);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> create(@RequestBody Genre genre, HttpServletRequest request) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if (genreRepository.existsByName(genre.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("genre name does  exist", null));
        }
        genre.setId(null);
        genre.setCreatedAt(LocalDateTime.now());
        genre.setCreatedBy(userReq);
        genre.setUpdatedAt(null);
        Genre newGenre = genreRepository.save(genre);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<Genre> response = new DetailResponse<>(newGenre, newGenre.getCreatedBy(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> update(@PathVariable Long id, @RequestBody Genre genre, HttpServletRequest request) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if (genreRepository.existsById(id)) {
            Genre genre1 = genreRepository.findById(id).orElse(null);
            genre1.setName(genre.getName());
            genre1.setUpdatedAt(LocalDateTime.now());
            genre1.setUpdatedBy(userReq);
            Genre genre2 = genreRepository.save(genre1);
            User createdBy = null;
            if (userRepository.existsById(genre1.getCreatedBy().getId())) {
                 createdBy = userRepository.findById(genre1.getCreatedBy().getId()).orElse(null);
              //  createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
          //  UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<Genre> response = new DetailResponse<>(genre2, createdBy, userReq);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Genre>>> delete(@PathVariable Long id) {
        if (genreRepository.existsById(id)) {
            Genre genre = genreRepository.findById(id).orElse(null);
            movieGenreRepository.deleteByGenre(genre);
            genreRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}
