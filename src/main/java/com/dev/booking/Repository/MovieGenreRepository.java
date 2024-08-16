package com.dev.booking.Repository;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    void deleteByMovie(Movie movie);
    void deleteByGenre(Genre genre);

    List<MovieGenre> findByMovie(Movie movie);

    boolean existsByMovieIdAndGenre(Long id, Genre genre);

    Optional<MovieGenre> findByMovieIdAndGenre(Long id, Genre genre);
}
