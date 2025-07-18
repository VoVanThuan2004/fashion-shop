package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reviewMessage;

    private int ratingStars;
    private LocalDateTime reviewTime;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageReview> imageReviews;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
