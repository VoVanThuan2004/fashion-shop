package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ReviewRequest {
    private int productId;
    private String reviewMessage;
    private int ratingStars;
}
