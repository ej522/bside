package com.example.beside.api.user;


import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @PostMapping(value = "/v1/users")
    public void createUser(@RequestBody @Validated CreateUserRequest requset) {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);
        
        userService.saveUser(user);
    }

    @GetMapping(value ="/v1/users")
    public List<UserDto> getAllUsers(){
        List<UserDto>UserDtoList = new ArrayList<>();

        List<User> userAll = userService.findUserAll();
        userAll.forEach( s -> {
            UserDto userDto = new UserDto(s.getId(), s.getPassword(), s.getEmail());
            UserDtoList.add(userDto);
        });
        return UserDtoList;
    }

    @Data
    static class CreateUserRequest{
        @NotNull
        private String email;
        @NotNull
        private String password;
    }
}
