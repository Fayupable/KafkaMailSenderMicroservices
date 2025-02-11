package com.fayupable.mailsender.repository;

import com.fayupable.mailsender.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<User,UUID> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
