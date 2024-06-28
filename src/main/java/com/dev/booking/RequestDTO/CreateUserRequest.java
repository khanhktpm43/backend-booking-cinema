package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private RegisterRequest user;
    private List<Role> roles;
}
