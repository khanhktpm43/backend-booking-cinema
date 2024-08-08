package com.dev.booking.Controller;

import com.dev.booking.RequestDTO.LoginDTO;
import com.dev.booking.RequestDTO.RefreshTokenRequest;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.TokenDTO;
import com.dev.booking.Service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auths")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenDTO>> login(@RequestBody LoginDTO loginDTO){
        TokenDTO tokenDTO = authService.login(loginDTO);
        if(tokenDTO != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",tokenDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Unauthorized: Invalid username or password",null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject<TokenDTO>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
       TokenDTO tokenDTO = authService.renewAccessToken(refreshTokenRequest);
       if(tokenDTO != null){
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", tokenDTO));
       }
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Invalid Refresh Token", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject<?>> logout(HttpServletRequest request, HttpServletResponse response, @RequestBody RefreshTokenRequest refreshTokenRequest){
        boolean result = authService.logout(request, response, refreshTokenRequest);
        if(result){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("success", null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("failed", null));
    }
}
