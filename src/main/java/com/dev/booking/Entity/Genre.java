package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "genre")
public class Genre extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "genre",cascade = CascadeType.ALL)
    private Set<MovieGenre> movieGenres = new HashSet<>();

}
