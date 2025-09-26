package com.assistant.authentication.controller;

import com.assistant.authentication.dto.AuthRequest;
import com.assistant.authentication.model.User;
import com.assistant.authentication.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@RequestBody User user) {
        log.info("Register Request" + user.toString());
        return authService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) throws Exception {
        log.info("Auth Request" + authRequest.toString());
        try{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            log.info("Authentication came");
            if (authenticate.isAuthenticated()) {
                log.info("Authentication done");
                return authService.generateToken(authRequest.getEmail());
            }
        }
        catch (Exception e){
            throw new RuntimeException("invalid access" + e);
        }
        return "Good to go";
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        if(authService.validateToken(token))
            return "Token is valid";

        return "Invalid Token";
    }

//    @PutMapping("/role")
//    public String updateUserRole(@RequestParam("email") String email, @RequestParam("role") String role) {
//        return authService.updateUserRole(email, role);
//    }
}
