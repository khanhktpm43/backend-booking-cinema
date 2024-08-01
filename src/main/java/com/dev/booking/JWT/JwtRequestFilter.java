package com.dev.booking.JWT;

import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.Service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Map<String, String> tokenAndUsername = getTokenAndUsernameFromRequest(request);
        String accessToken = (String) tokenAndUsername.get("accessToken");
        String username = (String) tokenAndUsername.get("username");
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(accessToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
    public Map<String,String> getTokenAndUsernameFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(accessToken);
        }
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("username", username);
        return response ;
    }

    public User getUserRequest(HttpServletRequest request){
        Map<String, String> tokenAndUsername = getTokenAndUsernameFromRequest(request);
        String username = (String) tokenAndUsername.get("username");
        return userRepository.findByUserName(username).orElse(null);
    }
}
