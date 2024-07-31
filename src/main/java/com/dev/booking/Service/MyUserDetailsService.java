package com.dev.booking.Service;

import com.dev.booking.Entity.MyUserDetails;
import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import com.dev.booking.Entity.UserRole;
import com.dev.booking.Repository.RoleRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.Repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<Role> roles = userRoles.stream()
                .map(userRole -> roleRepository.findById(userRole.getRole().getId())
                        .orElseThrow(() -> new IllegalStateException("Role not found")))
                .collect(Collectors.toList());
        return new MyUserDetails(user, roles);
    }
}
