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

    @ManyToOne
    @JoinColumn(name = "bookingID")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "foodID")
    private Food food;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private float price;

    @JsonIgnore
    private boolean deleted = false;


    // Getters and Setters
}
