package com.dev.booking.Service;

import com.dev.booking.Entity.MyUserDetails;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.JWT.JwtUtil;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.LoginDTO;
import com.dev.booking.RequestDTO.RefreshTokenRequest;
import com.dev.booking.ResponseDTO.TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Service

public class AuthService {
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BlackList list;
    @Autowired
    private JwtRequestFilter filter;
    public TokenDTO login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", myUserDetails.getId());
            claims.put("role", myUserDetails.getAuthorities());
            String accessToken = jwtUtil.generateToken(claims, userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(claims, userDetails.getUsername());
            return new TokenDTO(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            return null;
        }
    }

    public TokenDTO renewAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
        if(list.isTokenExists(refreshTokenRequest.getRefreshToken())){
            return null;
        }
        if (jwtUtil.validateToken(refreshTokenRequest.getRefreshToken(), username)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //   Long idFromToken = jwtUtil.extractClaim(refreshTokenRequest.getRefreshToken(), claims -> (Long) claims.get("id"));
            Long idFromToken = jwtUtil.extractClaim(refreshTokenRequest.getRefreshToken(), claims -> {
                Object idObject = claims.get("id");
                if (idObject instanceof Integer) {
                    return ((Integer) idObject).longValue();
                } else if (idObject instanceof Long) {
                    return (Long) idObject;
                } else {
                    throw new IllegalArgumentException("Unsupported ID type in JWT claims");
                }
            });
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", idFromToken);
            claims.put("role", userDetails.getAuthorities());
            String accessToken = jwtUtil.generateToken(claims, username);
            return new TokenDTO(accessToken, refreshTokenRequest.getRefreshToken());
        }
        return null;
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response, RefreshTokenRequest refreshTokenRequest) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Map<String, String> tokenAndUsername = filter.getTokenAndUsernameFromRequest(request);
            String accessToken = (String) tokenAndUsername.get("accessToken");
            list.addToken(accessToken, 1);
            list.addToken(refreshTokenRequest.getRefreshToken(), 10);
            logoutHandler.logout(request, response, authentication);
            return true;
        }
        return false;
    }
}
