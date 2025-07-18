package com.example.clothes_website.cloudinary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UploadResult {
    private String url;
    private String publicId;
}
