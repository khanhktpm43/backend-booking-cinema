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
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieGenreRepository movieGenreRepository;

    public List<DetailResponse<MovieGenre>> mapMovieGenreToResponse(List<MovieGenre> movieGenres) {
        return movieGenres.stream().map(movieGenre -> {
            UserBasicDTO createdBy = null;
            if (movieGenre.getCreatedBy() != null) {
                User user = userRepository.findById(movieGenre.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (movieGenre.getUpdatedBy() != null) {
                User user = userRepository.findById(movieGenre.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(movieGenre, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }

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
            movieGenre.setCreatedBy(userReq.getId());
            movieGenreRepository.save(movieGenre);
        }

        // Lấy lại Movie sau khi đã thêm Genre
        Movie result = movieRepository.findById(movieGenreDTO.getMovie().getId()).orElse(null);
        if (result == null) {
            return null; // Hoặc xử lý theo logic của bạn khi không tìm thấy Movie sau khi thêm Genre
        }

        // Tạo các đối tượng UserBasicDTO cho createdBy và updatedBy (nếu có)
        UserBasicDTO createdBy = null;
        UserBasicDTO updatedBy = null;
        if (result.getCreatedBy() != null) {
            User user = userRepository.findById(result.getCreatedBy()).orElse(null);
            if (user != null) {
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }
        if (result.getUpdatedBy() != null) {
            User user = userRepository.findById(result.getUpdatedBy()).orElse(null);
            if (user != null) {
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }

        // Tạo và trả về DetailResponse
        DetailResponse<Movie> response = new DetailResponse<>(result, createdBy, updatedBy);
        return response;
    }

//    public DetailResponse<Movie> attachGenres(HttpServletRequest request, MovieGenreDTO movieGenreDTO) {
//        User userReq = jwtRequestFilter.getUserRequest(request);
//        if (userReq == null || movieGenreDTO.getMovie().getId() == null || !movieRepository.existsById(movieGenreDTO.getMovie().getId()) || movieGenreDTO.getGenres().isEmpty())
//            return null;
//        Movie movie = movieRepository.findById(movieGenreDTO.getMovie().getId()).orElse(null);
//        for (Genre genre : movieGenreDTO.getGenres()) {
//            if (genre.getId() == null || !genreRepository.existsById(genre.getId()))
//                continue;
//            MovieGenre movieGenre = new MovieGenre();
//            movieGenre.setMovie(movie);
//            movieGenre.setGenre(genre);
//            movieGenre.setCreatedAt(LocalDateTime.now());
//            movieGenre.setUpdatedAt(null);
//            movieGenre.setCreatedBy(userReq.getId());
//            movieGenreRepository.save(movieGenre);
//        }
//        Movie result = movieRepository.findById(movieGenreDTO.getMovie().getId()).orElse(null);
//        UserBasicDTO createdBy = null;
//        UserBasicDTO updatedBy = null;
//        if (result != null && result.getCreatedBy() != null) {
//            User user = userRepository.findById(result.getCreatedBy()).orElse(null);
//            if (user != null)
//                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
//        }
//        if (result != null && result.getUpdatedBy() != null) {
//            User user = userRepository.findById(result.getUpdatedBy()).orElse(null);
//            if (user != null)
//                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
//        }
//        DetailResponse<Movie> response = new DetailResponse<>(result, createdBy, updatedBy);
//        return response;
//    }

    public DetailResponse<MovieGenre> update(User userReq, Long id, MovieGenre movieGenre) {
        MovieGenre movieGenre1 = movieGenreRepository.findById(id).orElse(null);
        movieGenre1.setMovie(movieGenre.getMovie());
        movieGenre1.setGenre(movieGenre.getGenre());
        movieGenre1.setUpdatedAt(LocalDateTime.now());
        movieGenre1.setUpdatedBy(userReq.getId());
        MovieGenre movieGenre2 = movieGenreRepository.save(movieGenre1);
        UserBasicDTO createdBy = null;
        if(movieGenre2.getCreatedBy() != null && userRepository.existsById(movieGenre2.getCreatedBy())){
            User user = userRepository.findById(movieGenre2.getCreatedBy()).orElse( null);
            createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<MovieGenre> response = new DetailResponse<>(movieGenre2, createdBy, updatedBy);
        return response;
    }
}
