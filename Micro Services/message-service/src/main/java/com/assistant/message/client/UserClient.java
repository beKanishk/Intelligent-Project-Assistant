package com.assistant.message.client;

import com.assistant.message.config.FeignConfig;
import com.assistant.message.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "USER-SERVICE", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/user/get-user/{email}")
    UserDto getUserByEmail(@PathVariable("email") String email);

}
