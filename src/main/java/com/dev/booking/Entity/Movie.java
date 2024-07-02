package com.dev.booking.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime releaseDate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Lob

    private String overview;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] trailer;

    @Column(nullable = false)
    private int duration;

    @JsonIgnore
    private Long createdBy;

    @JsonIgnore
    private Long updatedBy;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
   // @JsonBackReference("movie-genres-movie")
    private Set<MovieGenre> movieGenres;
    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
   // @JsonBackReference("movie-casts-movie")
    private Set<MovieCast> movieCasts;
    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference("movie-showtimes")
    private Set<Showtime> showtimes;

    public Movie(Long movieId, String movieName, LocalDateTime release, byte[] image, String overview, byte[] trailer, Integer duration, LocalDateTime createdAt, Long createdBy, LocalDateTime updatedAt, Long updatedBy) {
        this.id = movieId;
        this.name = movieName;
        this.releaseDate = release;
        this.image = image;
        this.overview = overview;
        this.trailer = trailer;
        this.duration = duration;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

//    @OneToMany(mappedBy = "movie")
//    @JsonBackReference
//    private Set<MovieGenre> movieGenres = new HashSet<>();
//
//    @OneToMany(mappedBy = "movie")
//    @JsonBackReference
//    private Set<MovieCast> movieCasts = new HashSet<>();

    // Getters and setters
}
