package com.dev.booking.Entity;

import com.dev.booking.Repository.UserRoleRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {
    @Getter
    private User user;
    @Getter
    private List<Role> roles;
    public MyUserDetails(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .collect(Collectors.toList());

    }
    @Override
    public String getPassword() {
        return user.getPassWord();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }
    public Long getId(){
        return this.user.getId();
    }
    public String getName(){
        return user.getName();
    }

    public String getEmail(){
        return user.getEmail();
    }
    public String getPhone(){
        return user.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
