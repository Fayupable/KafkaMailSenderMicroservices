package com.fayupable.mailsender.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fayupable.mailsender.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDto {
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private boolean enabled;
    private Set<Role> roles;
    private LocalDateTime createdAt;
}
