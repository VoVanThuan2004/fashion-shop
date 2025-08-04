package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.CartItemResponse;
import com.example.clothes_website.dto.response.CartResponse;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.repository.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private static final String SESSION_COOKIE_NAME = "cart_session";
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> getCart(Integer userId, HttpServletRequest request) {
        if (userId == null) {
            // 1. Lấy sessionId từ cookie
            String sessionId = Arrays.stream(
                            Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])
                    )
                    .filter(c -> SESSION_COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (sessionId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.builder()
                                .status("error")
                                .code(400)
                                .message("Không tìm thấy sessionId trong cookie")
                                .build()
                );
            }

            // 2. Tìm cart theo sessionId
            Optional<Cart> cartOptional = cartRepository.findCartBySessionId(sessionId);
            if (cartOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Giỏ hàng không tồn tại với sessionId: " + sessionId)
                                .build());
            }

            // 3. Chuyển sang response
            List<CartItemResponse> cartItemResponse = cartOptional.get().getCartItems().stream()
                    .map(item -> CartItemResponse.builder()
                            .cartItemId(item.getCartItemId())
                            .productVariantId(item.getProductVariant().getProductVariantId())
                            .productName(item.getProductVariant().getVariantName())
                            .productPrice(item.getProductVariant().getSellingPrice())
                            .productImage(item.getProductVariant().getVariantImageGroup().getImageVariants().get(0).getImagePath())
                            .quantity(item.getQuantity())
                            .build())
                    .toList();

            CartResponse cartResponse = CartResponse.builder()
                    .cartId(cartOptional.get().getCartId())
                    .totalQuantity(cartOptional.get().getTotalQuantity())
                    .totalPrice(cartOptional.get().getTotalPrice())
                    .cartItems(cartItemResponse)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Lấy thông tin giỏ hàng")
                            .data(cartResponse)
                            .build());
        }
        else {
            // 1. Kiểm tra user có tồn tại
            Optional<Users> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Người dùng không tồn tại")
                                .build());
            }

            // 2. Kiểm tra cart có tồn tại
            Optional<Cart> cartOptional = cartRepository.findCartByUserId(userId);
            if (cartOptional.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Giỏ hàng không tồn tại với userId: " + userId)
                                .build());
            }

            List<CartItemResponse> cartItemResponse = cartOptional.get().getCartItems().stream()
                    .map(item -> CartItemResponse.builder()
                            .cartItemId(item.getCartItemId())
                            .productVariantId(item.getProductVariant().getProductVariantId())
                            .productName(item.getProductVariant().getVariantName())
                            .productPrice(item.getProductVariant().getSellingPrice())
                            .productImage(item.getProductVariant().getVariantImageGroup().getImageVariants().get(0).getImagePath())
                            .quantity(item.getQuantity())
                            .build())
                    .toList();

            CartResponse cartResponse = CartResponse.builder()
                    .cartId(cartOptional.get().getCartId())
                    .cartItems(cartItemResponse)
                    .totalPrice(cartOptional.get().getTotalPrice())
                    .totalQuantity(cartOptional.get().getTotalQuantity())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Lấy thông tin giỏ hàng")
                            .data(cartResponse)
                            .build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> addCart(
            Integer userId, Integer productVariantId, Integer quantity, HttpServletRequest request, HttpServletResponse response
    ) {
        if (productVariantId == null || quantity == null) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Vui lòng nhập đầy đủ thông tin: productId, quantity")
                            .build());
        }

        Optional<ProductVariant> productVariant = productVariantRepository.findById(productVariantId);
        if (productVariant.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Sản phẩm không tồn tại với productVariantId: " + productVariantId)
                            .build()
            );
        }

        // Người dùng không đăng nhập
        if (userId == null) {
            // 1. Lấy sessionId từ cookie; nếu chưa có thì tạo mới
            String sessionId = Arrays.stream(
                    Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])
            )
                    .filter(c -> SESSION_COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseGet(() -> {
                        String newSession = UUID.randomUUID().toString();
                        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, newSession);
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setMaxAge(7 * 24 * 60 * 60);
                        response.addCookie(cookie);
                        return newSession;
                    });

            // 2. Tìm cart theo sessionId hoặc tạo mới nếu chưa có
            Cart cart = cartRepository.findCartBySessionId(sessionId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setSessionId(sessionId);
                        newCart.setCartItems(new ArrayList<>());
                        return cartRepository.save(newCart);
                    });

            // 3. Thêm product vào cart item
            addOrUpdateCartItem(Optional.of(cart), productVariant, quantity);

            return ResponseEntity.status(201)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(201)
                            .message("Thêm sản phẩm vào giỏ hàng thành công")
                            .build());

        }

        // Người dùng có đăng nhập
        else {
            // 1. Kiểm tra user có tồn tại
            Optional<Users> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Người dùng không tồn tại")
                                .build());
            }

            // 2. Kiểm tra giỏ hàng có tồn tại
            Optional<Cart> cart = cartRepository.findCartByUserId(userId);
            if (cart.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(404)
                                .message("Giỏ hàng không tồn tại với userId: " + userId)
                                .build());
            }

            // 3. Thêm product vào cart item
            addOrUpdateCartItem(cart, productVariant, quantity);

            return ResponseEntity.status(201)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(201)
                            .message("Thêm sản phẩm vào giỏ hàng thành công")
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteCart(Integer cartItemId) {
        if (cartItemId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Vui lòng nhập thông tin: cartItemId")
                            .build());
        }

        // 1. Kiểm tra cart item có tồn tại
        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Cart item không tồn tại")
                            .build());
        }

        // 2. Xóa cart item
        cartItemRepository.deleteById(cartItemId);

        // 3. Cập nhật lại cart
        Optional<Cart> cart = cartRepository.findById(cartItem.get().getCart().getCartId());
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Giỏ hàng không tồn tại")
                            .build());
        }

        cart.get().setTotalPrice(cart.get().getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProductVariant().getSellingPrice())
                .sum()
        );
        cart.get().setTotalQuantity(cart.get().getCartItems().size());
        cartRepository.save(cart.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateCartQuantity(Integer cartItemId, Integer quantity) {
        if (cartItemId == null || quantity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Vui lòng nhập thông tin: cartItemId, quantity")
                            .build());
        }

        if (quantity < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Số lượng phải lớn hơn bằng 1")
                            .build());
        }

        // 1. Kiểm tra cart item có tồn tại
        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Cart item không tồn tại")
                            .build());
        }

        // 2. Cập nhật lại số lượng
        cartItem.get().setQuantity(quantity);
        cartItemRepository.save(cartItem.get());

        // 3. Cập nhật lại Cart
        Cart cart = cartItem.get().getCart();
        cart.setTotalQuantity(cart.getCartItems().size());
        cart.setTotalPrice(cart.getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProductVariant().getSellingPrice())
                .sum()
        );
        cartRepository.save(cart);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Cập nhật số lượng sản phẩm trong giỏ hàng thành công")
                        .build());
    }


    private void addOrUpdateCartItem(Optional<Cart> cart, Optional<ProductVariant> productVariant, Integer quantity) {
        Optional<CartItem> cartItem = cart.get().getCartItems().stream()
                .filter(item -> item.getProductVariant().getProductVariantId() == productVariant.get().getProductVariantId())
                .findFirst();

        if (cartItem.isEmpty()) {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart.get());
            newCartItem.setProductVariant(productVariant.get());
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);

            cart.get().getCartItems().add(newCartItem);
            cart.get().setTotalQuantity(cart.get().getCartItems().size());
            cart.get().setTotalPrice(cart.get().getCartItems().stream()
                    .mapToDouble(item -> item.getQuantity() * item.getProductVariant().getSellingPrice())
                    .sum()
            );
            cartRepository.save(cart.get());
        }
        else {
            cart.get();
            cartItem.get().setQuantity(cartItem.get().getQuantity() + quantity);
            cartItemRepository.save(cartItem.get());

            cart.get().setTotalQuantity(cart.get().getCartItems().size());
            cart.get().setTotalPrice(cart.get().getCartItems().stream()
                    .mapToDouble(item -> item.getQuantity() * item.getProductVariant().getSellingPrice())
                    .sum()
            );
            cartRepository.save(cart.get());
        }
    }
}
