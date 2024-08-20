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
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User  extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(name = "user_name",nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String passWord;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy",fetch = FetchType.LAZY)
    private Set<User> createdUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "updatedBy",fetch = FetchType.LAZY)
    private Set<User> updatedUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings;



    public User(String name, String userName, String email, String phone, String passWord) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + super.getId() +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", passWord='" + passWord + '\'' +
                ", createdAt=" +super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                '}';
    }
}
