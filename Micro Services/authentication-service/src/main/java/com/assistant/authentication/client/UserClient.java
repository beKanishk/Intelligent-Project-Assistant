package com.assistant.authentication.client;

import com.assistant.authentication.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @PostMapping("/user/get-user/auth/{email}")
    User getUserByEmail(@PathVariable("email") String email);

    @PostMapping("/user/register")
    User saveUser(@RequestBody User user);
}

