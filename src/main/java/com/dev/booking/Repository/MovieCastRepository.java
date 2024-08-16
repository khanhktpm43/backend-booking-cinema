package com.dev.booking.Repository;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
    List<MovieCast> findByMovie(Movie movie);

    void deleteByMovie(Movie movie);
    void deleteByCast(Cast cast);

    boolean existsByMovieIdAndCastAndRoleCast(Long id, Cast cast, int roleCast);

    Optional<MovieCast> findByMovieIdAndCastAndRoleCast(Long id, Cast cast, int roleCast);
}
