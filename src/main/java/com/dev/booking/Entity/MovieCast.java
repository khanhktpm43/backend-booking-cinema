package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie_cast")
public class MovieCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    //@JsonManagedReference("movie-casts-movie")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "cast_id")
   // @JsonManagedReference("movie-casts-casts")
    private Cast cast;

    @Column(nullable = false)
    private int roleCast;

    // Getters and setters
}
