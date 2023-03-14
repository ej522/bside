package com.example.beside.api.user;


import com.example.beside.domain.TokenResponse;
import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

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

    @PostMapping(value = "/v1/users")
    public void createUser(@RequestBody @Validated CreateUserRequest requset) {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);
        
        userService.saveUser(user);
    }

    @DeleteMapping(value ="/v1/users")
    public void deleteUser(@RequestBody @Validated DeleteUserRequest request){

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(request.password);
        userService.deleteUser(user);
    }

    @PostMapping("/v1/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        String token = jwtProvider.createToken(request);

        return ResponseEntity.ok().body(new TokenResponse(token, "bearer"));
    }

    @Data
    static class CreateUserRequest{
        @NotNull
        private String email;
        @NotNull
        private String password;
    }

    @Data
    static class DeleteUserRequest{
        @NotNull
        private String email;
        @NotNull
        private String password;
    }
}
