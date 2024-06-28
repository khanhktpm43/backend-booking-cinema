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
@Table(name = "user-role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userID")
   // @JsonManagedReference("user-roles")
    private User user;

    @ManyToOne
    @JoinColumn(name = "roleID")
   // @JsonManagedReference("user-roles")
    private Role role;

    // Getters and Setters
}
