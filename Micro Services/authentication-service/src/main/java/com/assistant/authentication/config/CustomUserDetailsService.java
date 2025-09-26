package com.assistant.authentication.config;


import com.assistant.authentication.client.UserClient;
import com.assistant.authentication.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> details = userRepository.findByEmail(username);

        User details = userClient.getUserByEmail(username);
        log.info("User details" + details.toString());
        if (details == null) {
            throw new UsernameNotFoundException("User not found with name: " + username);
        }

        return new MappingUserDetails(details);
    }

}
