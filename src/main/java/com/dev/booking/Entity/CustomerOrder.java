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
@Table(name = "customer-order")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bookingID")
   // @JsonManagedReference("booking-orders")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "foodID")
   // @JsonManagedReference("food-orders")
    private Food food;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private float price;

    // Getters and Setters
}
