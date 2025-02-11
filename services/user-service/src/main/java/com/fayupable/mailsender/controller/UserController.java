package com.fayupable.mailsender.controller;

import com.fayupable.mailsender.dto.VerifyUserDto;
import com.fayupable.mailsender.request.AddUserRequest;
import com.fayupable.mailsender.request.LoginRequest;
import com.fayupable.mailsender.response.UserResponse;
import com.fayupable.mailsender.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody AddUserRequest request) {
        return ResponseEntity.ok(new UserResponse("User registered", userService.addUser(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new UserResponse("User logged in", userService.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<UserResponse> logout(HttpServletRequest request) {
        return ResponseEntity.ok(new UserResponse("User logged out", userService.logout(request)));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<UserResponse> validateToken(@RequestBody String request) {
        return ResponseEntity.ok(new UserResponse("Token validated", userService.validateToken(request)));
    }

    @PostMapping("/validate-token-from-header")
    public ResponseEntity<UserResponse> validateTokenFromHeader(HttpServletRequest request) {
        return ResponseEntity.ok(new UserResponse("Token validated", userService.validateTokenFromHeader(request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verifyUser(@RequestBody VerifyUserDto request) {
        return ResponseEntity.ok(new UserResponse("User verified", userService.verifyUser(request)));
    }


}
