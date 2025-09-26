package com.assistant.user.controller;

import com.assistant.user.config.JwtService;
import com.assistant.user.dto.Response;
import com.assistant.user.dto.UserDto;
import com.assistant.user.model.User;
import com.assistant.user.service.UserService;
import com.assistant.user.token.TokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto request) {
        log.info("UserDto" + request.toString());
        try {
            UserDto userDto = userService.register(
                    request.getEmail(),
                    request.getPassword(),
                    "USER",
                    request.getName()
            );
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/get-user/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        try {
            UserDto user = userService.getUser(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("cannot find user " + e.getMessage());
        }
    }

    @PostMapping("/get-user/auth/{email}")
    public ResponseEntity<?> getUserForAuthService(@PathVariable String email) {
        try {
            UserDto user = userService.getUser(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("cannot find user " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Response> getCurrentUser() {
        try{
            String token = TokenContext.getToken();
            token = token.substring(7);

            String email = jwtService.extractUsername(token);

            UserDto user = userService.getUser(email);

            if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            Response response = new Response(user.getId(), user.getEmail(), user.getName(), user.getRoles());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
