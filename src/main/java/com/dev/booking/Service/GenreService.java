package com.dev.booking.Service;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.GenreRepository;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GenreService {
    @Autowired
    private MovieGenreRepository movieGenreRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    public Page<DetailResponse<Genre>> getAll(int page, int size, String[] sort, String name) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Genre> genres;
        if (name == null || name.isEmpty()) {
            genres = genreRepository.findAll(pageable);
            return mappingService.mapToResponse(genres);
        }
        genres = genreRepository.findByNameContainingIgnoreCase(name, pageable);
        return mappingService.mapToResponse(genres);
    }

    public DetailResponse<Genre> getById(Long id) {
        Genre genre = genreRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(genre);
    }

    public DetailResponse<Genre> create(HttpServletRequest request, Genre genre) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        genre.setId(null);
        genre.setCreatedAt(LocalDateTime.now());
        genre.setCreatedBy(userReq);
        genre.setUpdatedAt(null);
        Genre newGenre = genreRepository.save(genre);
        return mappingService.mapToResponse(newGenre);
    }

    public DetailResponse<Genre> update(HttpServletRequest request, Long id, Genre genre) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Genre genre1 = genreRepository.findById(id).orElseThrow();
        genre1.setName(genre.getName());
        genre1.setUpdatedAt(LocalDateTime.now());
        genre1.setUpdatedBy(userReq);
        Genre genre2 = genreRepository.save(genre1);
        return mappingService.mapToResponse(genre2);
    }

    public void delete(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow();
        movieGenreRepository.deleteByGenre(genre);
        genreRepository.deleteById(id);
    }
}
