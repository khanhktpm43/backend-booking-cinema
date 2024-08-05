package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "food")
public class Food extends BaseEntity{

//    @Lob
//    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float price;

    @JsonIgnore
    private boolean deleted = false;


    @JsonIgnore
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomerOrder> customerOrders;

    // Getters and Setters
}

