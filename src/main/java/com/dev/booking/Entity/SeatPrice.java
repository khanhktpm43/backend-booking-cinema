package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat-price")
public class SeatPrice extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "seatTypeID")
    private SeatType seatType;

    private boolean normalDay;

    private boolean weekend;

    private boolean specialDay;

    private boolean earlyShow;

    @Column(nullable = false)
 //   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Column(nullable = false)

    private LocalDateTime endDate;

    @Column(nullable = false)
    private float price;


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

