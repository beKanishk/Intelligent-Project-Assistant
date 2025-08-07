package com.assistant.service;


import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assistant.model.User;
import com.assistant.repository.UserRepository;
import com.assistant.security.JwtUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public User findUserByJwt(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract user name (or email) from JWT
        String userEmail = jwtUtil.extractEmail(token);

        // Find user in database
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
	
	
	public User register(String email, String password, String role, String name) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email already registered");
		}
		String hashedPassword = passwordEncoder.encode(password);
		User user = new User(email, hashedPassword, role, name);
		return userRepository.save(user);
	}

	public String login(String email, String password) {
		Optional<User> userOpt = userRepository.findByEmail(email);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword())) {
				return jwtUtil.generateToken(email);
			}
		}
		throw new RuntimeException("Invalid credentials");
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
