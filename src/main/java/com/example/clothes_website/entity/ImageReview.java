package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ImageReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageReviewId;

    private String imagePath;
    private String publicId;    // Lưu id dùng để xóa image trên cloudinary

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;
}
