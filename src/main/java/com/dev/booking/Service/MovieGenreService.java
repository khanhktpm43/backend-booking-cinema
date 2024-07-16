package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.GenreRepository;
import com.dev.booking.Repository.MovieGenreRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.MovieGenreDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        DetailResponse<Movie> response = new DetailResponse<>(result, result.getCreatedBy(), result.getUpdatedBy());
        return response;
    }

    public DetailResponse<MovieGenre> update(User userReq, Long id, MovieGenre movieGenre) {
        MovieGenre movieGenre1 = movieGenreRepository.findById(id).orElse(null);
        movieGenre1.setMovie(movieGenre.getMovie());
        movieGenre1.setGenre(movieGenre.getGenre());
        movieGenre1.setUpdatedAt(LocalDateTime.now());
        movieGenre1.setUpdatedBy(userReq);
        MovieGenre movieGenre2 = movieGenreRepository.save(movieGenre1);
        DetailResponse<MovieGenre> response = new DetailResponse<>(movieGenre2, movieGenre2.getCreatedBy(), userReq);
        return response;
    }
}
