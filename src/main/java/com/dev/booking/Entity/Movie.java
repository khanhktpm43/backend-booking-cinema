package com.dev.booking.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Date;
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
    private Date releaseDate;

    @Lob
    private byte[] image;

    @Lob
    private String overview;

    @Lob
    private byte[] trailer;

    @Column(nullable = false)
    private int duration;

    @OneToMany(mappedBy = "movie")
    private Set<MovieGenre> movieGenres = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    private Set<MovieCast> movieCasts = new HashSet<>();

    // Getters and setters
}
