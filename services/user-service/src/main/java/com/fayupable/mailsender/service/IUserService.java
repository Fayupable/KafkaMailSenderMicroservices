package com.fayupable.mailsender.service;

import com.fayupable.mailsender.dto.UserDto;
import com.fayupable.mailsender.dto.VerifyUserDto;
import com.fayupable.mailsender.entity.User;
import com.fayupable.mailsender.request.AddUserRequest;
import com.fayupable.mailsender.request.EmailRequest;
import com.fayupable.mailsender.request.LoginRequest;
import com.fayupable.mailsender.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {

    UserResponse login(LoginRequest request);

    User logout(HttpServletRequest request);

    UserDto addUser(AddUserRequest request);

    boolean validateToken(String token);


    boolean validateTokenFromHeader(HttpServletRequest request);

    UserResponse verifyUser(VerifyUserDto verifyUserDto);

    void resendVerificationCode(EmailRequest request);
}
