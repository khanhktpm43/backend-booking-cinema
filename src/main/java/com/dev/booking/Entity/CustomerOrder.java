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
@Table(name = "customer-order")
public class CustomerOrder extends BaseEntity{

    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "bookingID")
    private Booking booking;

    @ManyToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "foodID")
    private Food food;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private float price;

    @JsonIgnore
    @Column(nullable = false)
    private boolean booked = true;
    // Getters and Setters
}
