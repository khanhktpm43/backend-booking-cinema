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
@Table(name = "movie_genre")
public class MovieGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    //@JsonManagedReference("movie-genres-movie")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "genre_id")
   // @JsonManagedReference("movie-genres-genres")
    private Genre genre;

    // Getters and setters
}
