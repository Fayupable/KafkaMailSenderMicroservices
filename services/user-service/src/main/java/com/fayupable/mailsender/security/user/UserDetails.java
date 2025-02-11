package com.fayupable.mailsender.security.user;

import com.fayupable.mailsender.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private String id;
    private String email;
    private String password;

    private Collection<GrantedAuthority> authorities;

    @Override
    public boolean isEnabled() {
        return org.springframework.security.core.userdetails.UserDetails.super.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return org.springframework.security.core.userdetails.UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return org.springframework.security.core.userdetails.UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return org.springframework.security.core.userdetails.UserDetails.super.isAccountNonExpired();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static UserDetails buildUserDetails(User userCredential) {
        List<GrantedAuthority> authorities = userCredential.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new UserDetails(
                userCredential.getUserId().toString(),
                userCredential.getEmail(),
                userCredential.getPassword(),
                authorities
        );
    }

    public static String getAuthenticatedUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    public static boolean isAdmin() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}