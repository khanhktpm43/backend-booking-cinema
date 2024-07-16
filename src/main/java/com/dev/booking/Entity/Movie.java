package com.dev.booking.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
public class Movie  extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime releaseDate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String overview;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String trailer;

    @Column(nullable = false)
    private int duration;

    @JsonIgnore
    private boolean deleted = false;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<MovieGenre> movieGenres;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<MovieCast> movieCasts;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Showtime> showtimes;

    public Movie(Long movieId, String movieName, LocalDateTime release, byte[] image, String overview, String trailer, Integer duration, LocalDateTime createdAt, User createdBy, LocalDateTime updatedAt, User updatedBy) {
        super(movieId,createdBy, updatedBy,createdAt, updatedAt);
        this.name = movieName;
        this.releaseDate = release;
        this.image = image;
        this.overview = overview;
        this.trailer = trailer;
        this.duration = duration;
    }


    // Getters and setters
}
