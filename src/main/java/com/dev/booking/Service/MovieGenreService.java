package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.GenreRepository;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.RequestDTO.MovieGenreDTO;
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
public class MovieGenreService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieGenreRepository movieGenreRepository;
    @Autowired
    private MappingService mappingService;

    public DetailResponse<Movie> attachGenres(HttpServletRequest request, MovieGenreDTO movieGenreDTO) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null || movieGenreDTO.getMovie().getId() == null || !movieRepository.existsById(movieGenreDTO.getMovie().getId()) || movieGenreDTO.getGenres().isEmpty()) {
            return null;
        }
        Movie movie = movieRepository.findById(movieGenreDTO.getMovie().getId()).orElse(null);
        if (movie == null) {
            return null;
        }
        for (Genre genre : movieGenreDTO.getGenres()) {
            if (genre.getId() == null || !genreRepository.existsById(genre.getId())) {
                continue;
            }
            Genre managedGenre = genreRepository.findById(genre.getId()).orElse(null);
            if (managedGenre == null) {
                continue;
            }
            MovieGenre movieGenre = new MovieGenre();
            movieGenre.setMovie(movie);
            movieGenre.setGenre(managedGenre);
            movieGenre.setCreatedAt(LocalDateTime.now());
            movieGenre.setUpdatedAt(null);
            movieGenre.setCreatedBy(userReq);
            movieGenreRepository.save(movieGenre);
        }
        Movie result = movieRepository.findById(movieGenreDTO.getMovie().getId()).orElse(null);
        if (result == null) {
            return null;
        }
        DetailResponse<Movie> response = mappingService.mapToResponse(result);
        //new DetailResponse<>(result, result.getCreatedBy(), result.getUpdatedBy());
        return response;
    }

    public DetailResponse<MovieGenre> update(HttpServletRequest request, Long id, Genre genre) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        MovieGenre movieGenre1 = movieGenreRepository.findById(id).orElseThrow();
        // movieGenre1.setMovie(movieGenre.getMovie());
        movieGenre1.setGenre(genre);
        movieGenre1.setUpdatedAt(LocalDateTime.now());
        movieGenre1.setUpdatedBy(userReq);
        MovieGenre movieGenre2 = movieGenreRepository.save(movieGenre1);
        return mappingService.mapToResponse(movieGenre2);
    }

    public Page<DetailResponse<MovieGenre>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<MovieGenre> movieGenres = movieGenreRepository.findAll(pageable);
        return mappingService.mapToResponse(movieGenres);
    }

    public DetailResponse<MovieGenre> getById(Long id) {
        MovieGenre movieGenre = movieGenreRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(movieGenre);
    }
}
