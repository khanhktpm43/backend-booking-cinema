package com.dev.booking.Entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "user_name",nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passWord;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "updatedBy")
    private User updatedBy;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @JsonIgnore
//    @OneToMany(mappedBy = "createdBy")
@OneToMany(mappedBy = "createdBy")
//@JsonIgnoreProperties({"createdBy"})
    private Set<User> createdUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "updatedBy")
    private Set<User> updatedUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  //  @JsonBackReference("user-roles")
    private Set<UserRole> userRoles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
 //   @JsonBackReference("user-bookings")
    private Set<Booking> bookings;

    public User(String name, String userName, String email, String phone, String passWord) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.passWord = passWord;
    }

    // Getters and Setters
}
