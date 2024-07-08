package com.dev.booking.Repository;

import com.dev.booking.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
