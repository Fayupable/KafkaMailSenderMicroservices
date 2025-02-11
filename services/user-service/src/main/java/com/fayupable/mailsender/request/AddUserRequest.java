package com.fayupable.mailsender.request;

import lombok.Data;

@Data
public class AddUserRequest {
    private String email;

    private String username;

    private String password;


}
