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
@Table(name = "room")
public class Room  extends BaseEntity{


    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;


    @JsonIgnore
    private boolean deleted = false;



//    @JsonIgnore
//    private Long createdBy;
//
//    @JsonIgnore
//    private Long updatedBy;
//
//    @Column(nullable = true)
//    private LocalDateTime createdAt;
//
//    @Column(nullable = true)
//    private LocalDateTime updatedAt;
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference("room-seats")
    private Set<Seat> seats;
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference("room-showtimes")
    private Set<Showtime> showtimes;


    // Getters and Setters
}
