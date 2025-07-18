package com.example.clothes_website.websocket;

import com.example.clothes_website.dto.response.ImageReviewResponse;
import com.example.clothes_website.dto.response.ReviewResponse;
import com.example.clothes_website.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewSocket {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastNewReview(Review review) {
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .reviewMessage(review.getReviewMessage())
                .ratingStars(review.getRatingStars())
                .reviewTime(review.getReviewTime())
                .imageReviewResponse(review.getImageReviews() != null ? review.getImageReviews().stream()
                        .map(imageReview -> ImageReviewResponse.builder()
                                .imageReviewId(imageReview.getImageReviewId())
                                .imagePath(imageReview.getImagePath())
                                .build())
                        .toList()
                        : null
                )
                .build();

        messagingTemplate.convertAndSend("/topic/review/" + review.getProduct().getProductId(), reviewResponse);
    }
}
