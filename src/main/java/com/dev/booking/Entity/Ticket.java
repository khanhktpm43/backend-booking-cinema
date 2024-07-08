package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtimeID")
   // @JsonManagedReference("showtime-tickets")
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seatID")
  //  @JsonManagedReference("seat-tickets")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "bookingID")
   // @JsonManagedReference("booking-tickets")
    private Booking booking;

    @Column(nullable = false)
    private float price;
    @JsonIgnore
    private Long createdBy;

    @JsonIgnore
    private Long updatedBy;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    // Getters and Setters
}

