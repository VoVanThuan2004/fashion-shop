package com.example.clothes_website.security;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayload {
    private int userId;
    private String fullName;
    private String role;
}
