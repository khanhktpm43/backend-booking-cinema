package com.dev.booking.Repository;

import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import com.dev.booking.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);

    boolean existsByUserAndRole(User user, Role role);
}
