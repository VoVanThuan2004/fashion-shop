package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ImageReviewResponse {
    private int imageReviewId;
    private String imagePath;
}
