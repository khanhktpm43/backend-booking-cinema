package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat-price")
public class SeatPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seatTypeID")
   // @JsonManagedReference("seat-type-prices")
    private SeatType seatType;
    private boolean normalDay;
    private boolean weekend;
    private boolean specialDay;
    private boolean earlyShow;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

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
    @JsonIgnore
    public boolean isValid(){
        int intNormal = normalDay ? 1 : 0;
        int intWeekend = weekend ? 1 : 0;
        int intSpecial = specialDay ? 1 : 0;
        int intEarly = earlyShow ? 1 : 0;
        return intSpecial + intNormal + intWeekend + intEarly == 1;
    }
}

