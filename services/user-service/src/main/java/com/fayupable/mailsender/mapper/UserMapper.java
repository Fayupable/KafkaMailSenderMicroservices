package com.fayupable.mailsender.mapper;

import com.fayupable.mailsender.dto.UserDto;
import com.fayupable.mailsender.entity.User;
import com.fayupable.mailsender.request.AddUserRequest;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(AddUserRequest addUserRequest) {
        User user = new User();
        user.setEmail(addUserRequest.getEmail());
        user.setUsername(addUserRequest.getUsername());
        user.setPassword(addUserRequest.getPassword());
        return user;
    }

    public UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles());
        userDto.setEnabled(user.isEnabled());
        userDto.setPassword(user.getPassword());
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }

}
