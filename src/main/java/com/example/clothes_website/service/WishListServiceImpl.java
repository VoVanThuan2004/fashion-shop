package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.ProductResponse;
import com.example.clothes_website.entity.Product;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.entity.WishList;
import com.example.clothes_website.entity.WishListItem;
import com.example.clothes_website.repository.ProductRepository;
import com.example.clothes_website.repository.UserRepository;
import com.example.clothes_website.repository.WishListItemRepository;
import com.example.clothes_website.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
    private final UserRepository userRepository;
    private final WishListItemRepository wishListItemRepository;
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
                size,
                Sort.by("addedTime").descending()
        );


        int wishListId = user.get().getWishlist().getWishListId();

        // Lấy danh sách sản phẩm có trong wishList
        Page<Product> products = wishListItemRepository.getAllProductsBelongToWishList(pageable, wishListId);

        Page<ProductResponse> productResponses = products.map(
                product -> ProductResponse.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription())
                        .imagePath(product.getImagePath())
                        .isActive(product.getIsActive())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Danh sách sản phẩm có trong danh mục yêu thích của người dùng")
                        .data(productResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addProductToWishList(int productId, int userId) {
        // Kiểm tra product
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Sản phẩm với ID: " + productId + " không tồn tại")
                            .build());
        }

        // Kiểm tra user
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Người dùng không hợp lệ")
                            .build());
        }

        // Kiểm tra xem product này có trong wishlist chưa
        // Nếu có -> gỡ ra khỏi wishlist, ngược lại -> thêm vào
        WishList wishList = user.get().getWishlist();
        Optional<WishListItem> wishListItemExisting = wishListItemRepository.findByWishListIdAndProductId(
                wishList.getWishListId(),
                productId
        );

        if (wishListItemExisting.isPresent()) {
            wishListItemRepository.delete(wishListItemExisting.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Sản phẩm đã được xóa khỏi danh mục yêu thích")
                            .build());
        }
        else {
            WishListItem wishListItem = new WishListItem();
            wishListItem.setWishList(wishList);
            wishListItem.setProduct(product.get());
            wishListItem.setAddedTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            wishListItemRepository.save(wishListItem);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(HttpStatus.CREATED.value())
                            .message("Sản phẩm đã được thêm vào danh mục yêu thích")
                            .build());
        }
    }
}
