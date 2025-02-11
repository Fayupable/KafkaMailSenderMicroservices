package com.fayupable.mailsender.enums;

import lombok.Getter;

public enum EmailTemplates {
    USER_VERIFICATION("user_verification.html","User Verification Successful"),
    USER_LOGIN("user_login.html","User Login Successful");

    @Getter
    private final String template;
    @Getter
    private final String subject;

    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
