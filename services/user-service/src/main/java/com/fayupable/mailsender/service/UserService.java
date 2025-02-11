package com.fayupable.mailsender.service;

import com.fayupable.mailsender.dto.UserDto;
import com.fayupable.mailsender.dto.VerifyUserDto;
import com.fayupable.mailsender.entity.User;
import com.fayupable.mailsender.enums.Role;
import com.fayupable.mailsender.kafka.UserConfirmation;
import com.fayupable.mailsender.kafka.UserProducer;
import com.fayupable.mailsender.mapper.UserMapper;
import com.fayupable.mailsender.repository.IUserRepository;
import com.fayupable.mailsender.request.AddUserRequest;
import com.fayupable.mailsender.request.EmailRequest;
import com.fayupable.mailsender.request.LoginRequest;
import com.fayupable.mailsender.response.JwtResponse;
import com.fayupable.mailsender.response.UserResponse;
import com.fayupable.mailsender.security.jwt.JwtUtils;
import com.fayupable.mailsender.security.user.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserProducer userProducer;

    @Override
    public UserResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateTokenForUser(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(request.getEmail());
            sendUserLoginMessage(user);

            return new UserResponse("Login success", new JwtResponse(userDetails.getId(), jwt));
        } catch (UsernameNotFoundException e) {
            return new UserResponse("Invalid credentials", null);
        } catch (Exception e) {
            return new UserResponse("Login failed: " + e.getMessage(), null);
        }
    }

    @Override
    public User logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtUtils.blacklistToken(token);

            SecurityContextHolder.clearContext();
            return null;
        }
        return null;
    }

    @Override
    @Transactional
    public UserDto addUser(AddUserRequest request) {
        User savedUser = Optional.of(request)
                .map(this::createUserHelper)
                .map(user -> {
                    checkUserNameExist(user);
                    checkEmailExist(user);
                    return user;
                })
                .map(userRepository::save)
                .orElseThrow(() -> new RuntimeException("Error adding user"));
        sendUserConfirmationMessage(savedUser);
        return userMapper.fromUser(savedUser);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (jwtUtils.isTokenBlacklisted(token)) {
                return false;
            }
            return jwtUtils.validateToken(token);
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }

    @Override
    public boolean validateTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return validateToken(token);
        }
        return false;
    }

    @Transactional
    @Override
    public UserResponse verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepository.findByEmail(verifyUserDto.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiration(null);
            userRepository.save(user);
            return new UserResponse("User verified", userMapper.fromUser(user));
        } else {
            throw new RuntimeException("Invalid verification code");
        }
    }


    @Transactional
    @Override
    public void resendVerificationCode(EmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
    }


    private User createUserHelper(AddUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(5));
        assignRoleToUser(user);
        return user;
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private void assignRoleToUser(User user) {
        user.setRoles(Set.of(Role.ROLE_USER));
    }

    private void checkUserNameExist(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
    }

    private void checkEmailExist(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }

    private void sendUserConfirmationMessage(User user) {
        UserConfirmation userConfirmation = new UserConfirmation();
        userConfirmation.setUserId(user.getUserId());
        userConfirmation.setEmail(user.getEmail());
        userConfirmation.setVerificationCode(user.getVerificationCode());
        userConfirmation.setVerificationCodeExpiration(user.getVerificationCodeExpiration());
        userProducer.sendConfirmation(userConfirmation);
    }

    private void sendUserLoginMessage(User user) {
        UserConfirmation userLogin = new UserConfirmation();
        userLogin.setUserId(user.getUserId());
        userLogin.setEmail(user.getEmail());
        userLogin.setUserLoginTime(LocalDateTime.now());
        userProducer.sendUserLogin(userLogin);
    }
}
