package com.example.clothes_website.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
@AllArgsConstructor
public class ReviewResponse {
    private int reviewId;
    private String fullName;
    private String imageAvatar;
    private String reviewMessage;
    private int ratingStars;
    private LocalDateTime reviewTime;
    private List<ImageReviewResponse> imageReviewResponse;

}
