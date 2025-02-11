package com.fayupable.mailsender.security.user;

import com.fayupable.mailsender.entity.User;
import com.fayupable.mailsender.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final IUserRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userCredential = Optional.ofNullable(userCredentialRepository.findByEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDetails.buildUserDetails(userCredential);
    }
}