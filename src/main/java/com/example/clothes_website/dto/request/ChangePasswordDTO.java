package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
