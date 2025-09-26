package com.assistant.user.service;

import com.assistant.user.dto.UserDto;
import com.assistant.user.model.User;
import com.assistant.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto register(String email, String password, String role, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
//        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, name, password, List.of(role));
        User saved = userRepository.save(user);

        return new UserDto(saved.getEmail(), saved.getName(), saved.getRole());
    }

    public UserDto getUser(String email){
        try{
            Optional<User> optUser = userRepository.findByEmail(email);
            User user = optUser.get();
            return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
