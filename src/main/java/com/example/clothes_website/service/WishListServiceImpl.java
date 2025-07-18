package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.entity.WishList;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.UserRepository;
import com.example.clothes_website.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
    private final UserRepository userRepository;
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> getWishList(int userId, int page, int size) {
        // Check user
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Người dùng với ID: " + userId + " không tồn tại")
                            .build());
        }

        // Phân trang
        Pageable pageable = PageRequest.of(
                page,
                size
        );


        WishList wishList = user.get().getWishlist();

        List<ProductResponse> productResponses = wishList.getProducts().stream()
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .imagePath(product.getImagePath())
                        .description(product.getDescription())
                        .isActive(product.getIsActive())
                        .build())
                .toList();

        return null;
    }
}
