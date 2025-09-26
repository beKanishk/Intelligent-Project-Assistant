package com.assistant.session.client;

import com.assistant.session.config.FeignConfig;
import com.assistant.session.dto.UserDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "USER-SERVICE", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/user/get-user/{email}")
    UserDto getUserByEmail(@PathVariable("email") String email);

}
