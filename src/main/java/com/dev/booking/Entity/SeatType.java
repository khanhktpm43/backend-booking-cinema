package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat-type")
public class SeatType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "seatType", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference("seat-type-prices")
    private Set<SeatPrice> seatPrices;

    @JsonIgnore
    @OneToMany(mappedBy = "seatType", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference("seat-type-seats")
    private Set<Seat> seats;

    // Getters and Setters
}
