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
public class Ticket extends BaseEntity{

    @ManyToOne()
    @JoinColumn(name = "showtimeid")
    private Showtime showtime;

    @ManyToOne()
    @JoinColumn(name = "seatid")
    private Seat seat;

    @ManyToOne()
    @JoinColumn(name = "bookingid")
    private Booking booking;

    @Column(nullable = false)
    private float price;

    @JsonIgnore
    @Column(nullable = false)
    private boolean booked = true;

    // Getters and Setters
}

