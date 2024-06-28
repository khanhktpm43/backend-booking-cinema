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

    // Getters and Setters
}

