package com.dev.booking.Controller;

import com.dev.booking.RequestDTO.LoginDTO;
import com.dev.booking.RequestDTO.RefreshTokenRequest;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.TokenDTO;
import com.dev.booking.Service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auths")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenDTO>> login(@RequestBody LoginDTO loginDTO){
        TokenDTO tokenDTO = authService.login(loginDTO);
        if(tokenDTO != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<TokenDTO>("",tokenDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<TokenDTO>("Unauthorized: Invalid username or password",null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject<TokenDTO>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
       TokenDTO tokenDTO = authService.renewAccessToken(refreshTokenRequest);
       if(tokenDTO != null){
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<TokenDTO>("", tokenDTO));
       }

        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<TokenDTO>("Invalid Refresh Token", tokenDTO));
    }
}
