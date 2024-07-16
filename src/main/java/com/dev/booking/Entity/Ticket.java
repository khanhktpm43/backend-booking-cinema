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

