package com.fayupable.mailsender.entity;

import com.fayupable.mailsender.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expiration")
    private LocalDateTime verificationCodeExpiration;

    @Column(name = "reset_password_code")
    private String resetPasswordCode;

    @Column(name = "reset_password_code_expiration")
    private LocalDateTime resetPasswordCodeExpiration;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


}
