package com.dev.booking.Service;

import com.dev.booking.JWT.JwtUtil;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.LoginDTO;
import com.dev.booking.RequestDTO.RefreshTokenRequest;
import com.dev.booking.ResponseDTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;


    public TokenDTO login(LoginDTO loginDTO){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            return new TokenDTO(accessToken,refreshToken);
        } catch (AuthenticationException e){

            return null;
        }
    }
    public TokenDTO renewAccessToken(RefreshTokenRequest refreshTokenRequest){
        String username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
        if(jwtUtil.validateToken(refreshTokenRequest.getRefreshToken(), username)){
            String accessToken = jwtUtil.generateToken(username);
            return new TokenDTO(accessToken,refreshTokenRequest.getRefreshToken());
        }
        return null;
    }
}
