package com.dev.booking.Entity;

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
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seatID")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "bookingID")
    private Booking booking;

    @Column(nullable = false)
    private float price;

    // Getters and Setters
}

