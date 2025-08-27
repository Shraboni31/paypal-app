package com.paypal.user_service.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String adminKey;

    public SignupRequest(String name, String email, String password, String adminKey) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.adminKey = adminKey;
    }
}
