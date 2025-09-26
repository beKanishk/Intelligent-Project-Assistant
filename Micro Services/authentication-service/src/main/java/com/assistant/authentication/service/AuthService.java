package com.assistant.authentication.service;

import com.assistant.authentication.client.UserClient;
import com.assistant.authentication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserClient userClient;

    public String saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRoles(List.of("MESSAGE_READER"));
        userClient.saveUser(user);
        return "User added to the system";
    }

//    public String updateUserRole(String username, String newRole) {
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<String> roles = user.getRoles();
//        if (!roles.contains(newRole)) {
//            roles.add(newRole);
//            user.setRoles(roles);
//            userRepository.save(user);
//        }
//
//        return "Role " + newRole + " assigned to user " + username;
//    }

    public boolean validateToken(String token){
        if(jwtService.validateToken(token)){
            return true;
        }
        return false;
    }

    public String generateToken(String username) throws Exception {
        return jwtService.generateToken(username);
    }
}
