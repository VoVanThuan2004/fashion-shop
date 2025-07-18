package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class UserCreate {
    private String email;
    private String fullName;
    private String password;

}
