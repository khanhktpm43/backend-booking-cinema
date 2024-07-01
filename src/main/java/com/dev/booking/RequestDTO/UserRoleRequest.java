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
public class UserRoleRequest {
    private User user;
    private List<Role> roles;
}
