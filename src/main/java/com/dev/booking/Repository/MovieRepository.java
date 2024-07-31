package com.dev.booking.Repository;

import com.dev.booking.Entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = "SELECT m.id, m.duration, m.image, m.name, m.overview, m.release_date, m.trailer, m.created_at, m.created_by, m.updated_at, m.updated_by , g.id as g_id, g.name as g_name , c.id as c_id, c.name as c_name, mc.role_cast " +
            "FROM movie m " +
            "LEFT JOIN movie_genre mg ON m.id = mg.movie_id " +
            "LEFT JOIN genre g ON mg.genre_id = g.id\n" +
            "LEFT JOIN movie_cast mc ON m.id = mc.movie_id " +
            "LEFT JOIN `cast` c ON mc.cast_id = c.id " +
            "WHERE m.id =:movieId",
            nativeQuery = true)
    List<Object[]> findDetailById(@Param("movieId") Long movieId);

    Page<Movie> findByDeleted(boolean b, Pageable pageable);

    Optional<Movie> findByIdAndDeleted(Long id, boolean b);

    boolean existsByIdAndDeleted(Long id, boolean b);
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.showtimes s " +
            "WHERE s.startTime > :currentTime AND s.deleted = false AND m.deleted = false")
    List<Movie> findMoviesWithActiveShowtimes(@Param("currentTime") LocalDateTime currentTime);
    @Query("SELECT m FROM Movie m " +
            "WHERE m.releaseDate > :currentTime " +
            "AND m.deleted = false " +
            "AND NOT EXISTS (SELECT s FROM Showtime s WHERE s.movie = m AND s.deleted = false)")
    List<Movie> findMoviesUpcoming(@Param("currentTime") LocalDateTime currentTime);

    Page<Movie> findByNameContainingIgnoreCaseAndDeleted(String name, boolean b, Pageable pageable);
}
