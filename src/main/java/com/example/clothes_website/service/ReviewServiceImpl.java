package com.example.clothes_website.service;

import com.example.clothes_website.cloudinary.CloudinaryService;
import com.example.clothes_website.cloudinary.UploadResult;
import com.example.clothes_website.dto.request.ReviewRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ImageReviewResponse;
import com.example.clothes_website.dto.response.ReviewResponse;
import com.example.clothes_website.entity.ImageReview;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.Review;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.repository.ImageReviewRepository;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.ReviewRepository;
import com.example.clothes_website.repository.UserRepository;
import com.example.clothes_website.websocket.ReviewSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewSocket reviewSocket;
    private final CloudinaryService cloudinaryService;
    private final ImageReviewRepository imageReviewRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> addReview(ReviewRequest reviewRequest, List<MultipartFile> images, int userId) {
        // Check user
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Người dùng với ID: " + userId + " không tồn tại")
                            .build());
        }

        // Check product
        Optional<Product> product = productRepository.findById(reviewRequest.getProductId());
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm không tồn tại với ID: " + reviewRequest.getProductId())
                            .build());
        }

        Review review = new Review();
        review.setReviewMessage(reviewRequest.getReviewMessage());
        review.setRatingStars(reviewRequest.getRatingStars());
        review.setReviewTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        review.setUser(user.get());
        review.setProduct(product.get());
        review = reviewRepository.save(review);


        // Check nếu có images thì lưu
        if (images != null && !images.isEmpty()) {
            List<ImageReview> imageReviews = new ArrayList<>();
            for (MultipartFile image : images) {
                try {
                    UploadResult uploadResult = cloudinaryService.uploadCloudinaryImage(image);
                    ImageReview imageReview = new ImageReview();
                    imageReview.setPublicId(uploadResult.getPublicId());
                    imageReview.setImagePath(uploadResult.getUrl());
                    imageReview.setReview(review);

                    imageReviews.add(imageReview);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.builder()
                                    .status("error")
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Lỗi upload ảnh: " + e.getMessage())
                                    .build());
                }
            }

            imageReviewRepository.saveAll(imageReviews);
            review.setImageReviews(imageReviews);
        }

        // Gửi qua web socket
        reviewSocket.broadcastNewReview(review);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.CREATED.value())
                        .message("Review đã được đăng thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllReviews(int productId, int page, int size) {
        // Check product
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Sản phẩm không tồn tại với ID: " + productId)
                            .build());
        }

        // Sắp xếp giảm dần theo thời gian
        Pageable sortedPageable = PageRequest.of(
                page,
                size,
                Sort.by("reviewTime").descending()
        );

        Page<Review> reviews = reviewRepository.findByProductId(productId, sortedPageable);

        // Chuyển sang response
        Page<ReviewResponse> reviewResponses = reviews
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .fullName(review.getUser().getFullName())
                        .imageAvatar(review.getUser().getImage())
                        .reviewMessage(review.getReviewMessage())
                        .ratingStars(review.getRatingStars())
                        .reviewTime(review.getReviewTime())
                        .imageReviewResponse(review.getImageReviews().stream()
                                .map(imageReview -> ImageReviewResponse.builder()
                                        .imageReviewId(imageReview.getImageReviewId())
                                        .imagePath(imageReview.getImagePath())
                                        .build())
                                .toList()
                        )
                        .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Danh sách reviews của sản phẩm với ID: " + productId)
                        .data(reviewResponses)
                        .build());
    }
}
