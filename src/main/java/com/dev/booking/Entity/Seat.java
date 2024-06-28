package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "seat_row", nullable = false)
    private String row;

    @Column(name = "seat_column", nullable = false)
    private int column;

    @ManyToOne
    @JoinColumn(name = "typeID")
    //@JsonManagedReference("seat-type-seats")
    private SeatType seatType;

    @ManyToOne
    @JoinColumn(name = "roomID")
   // @JsonManagedReference("room-seats")
    private Room room;

    @JsonIgnore
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
  //  @JsonBackReference("seat-tickets")
    private Set<Ticket> tickets;


    // Getters and Setters
}

