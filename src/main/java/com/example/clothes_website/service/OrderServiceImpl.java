package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.OrderRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.response.OrderItemResponse;
import com.example.clothes_website.dto.response.OrderResponse;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.enums.OrderStatus;
import com.example.clothes_website.enums.PaymentMethod;
import com.example.clothes_website.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> placeOrder(Integer userId, OrderRequest orderRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        // Kiểm tra người dùng
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Người dùng không tồn tại với ID: " + userId)
                            .build());
        }

        // Kiểm tra thông tin body
        if (orderRequest.getCartId() == null || orderRequest.getPaymentMethod() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Vui lòng nhập đầy đủ thông tin: cartId, paymentMethod")
                            .build());
        }

        // Kiểm tra giỏ hàng có tồn tại
        Optional<Cart> cart = cartRepository.findById(orderRequest.getCartId());
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Giỏ hàng không tồn tại với ID: " + userId)
                            .build());
        }
        if (cart.get().getCartItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Giỏ hàng hiện tại đang trống")
                            .build());
        }

        // Kiểm tra số lượng đặt phải luôn nhỏ hơn số lượng còn trong kho
        List<CartItem> cartItems = cart.get().getCartItems();
        for (CartItem cartItem : cartItems) {
            int quantity = cartItem.getQuantity();
            if (quantity > cartItem.getProductVariant().getInventory().getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message("Sản phẩm " + cartItem.getProductVariant().getVariantName() + " không đủ số lượng hàng để đặt")
                                .build());
            }
        }

        // Kiểm tra có sử dụng mã coupon không
        Optional<Coupon> coupon = Optional.empty();
        if (orderRequest.getCouponId() != null) {
            coupon = couponRepository.findById(orderRequest.getCouponId());
            if (coupon.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.NOT_FOUND.value())
                                .message("Mã giảm giá không tồn tại với ID: " + orderRequest.getCouponId())
                                .build());
            }
        }

        // Kiểm tra điểm tích lũy nhập vào phải nhỏ hơn đang có
        if (orderRequest.getLoyaltyPoints() != null && orderRequest.getLoyaltyPoints() > user.get().getLoyaltyPoints()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Điểm tích lũy hiện tại không đủ")
                            .build());
        }


        // Thanh toán
        Integer totalQuantity = cart.get().getTotalQuantity();
        Double totalPrice = cart.get().getTotalPrice();
        String fullName = user.get().getFullName();
        String shippingAddress = getFullAddress(user.get().getAddresses());

        switch (orderRequest.getPaymentMethod()) {
            // TH1. Thanh toán tiền mặt (COD)
            case CASH:
                // Tạo Order
                Order order = new Order();
                order.setUser(user.get());
                order.setFullName(fullName);
                order.setShippingAddress(shippingAddress);
                order.setCoupon(coupon.orElse(null));
                order.setCouponCode(coupon.map(Coupon::getCouponCode).orElse(null));
                order.setDiscountPrice(coupon.map(Coupon::getDiscountPrice).orElse(null));
                order.setLoyaltyPoints(orderRequest.getLoyaltyPoints());
                order.setTotalQuantity(totalQuantity);
                order.setTotalPrice(totalPrice);
                order.setPaymentMethod(PaymentMethod.CASH);
                order.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                order.setCurrentStatus(OrderStatus.PENDING);
                order = orderRepository.save(order);

                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setOrder(order);
                orderHistory.setCreateTime(order.getCreateTime());
                orderHistory.setOrderStatus(OrderStatus.PENDING);
                orderHistoryRepository.save(orderHistory);

                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem cartItem: cartItems) {
                    // Kiểm tra số lượng kho hiện tại để đặt hàng
                    if (cartItem.getProductVariant().getInventory().getQuantity() < 1) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.builder()
                                        .status("error")
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Sản phẩm: " +
                                            cartItem.getProductVariant().getVariantName() + " hết hàng"
                                        )
                                        .build());
                    }
                    OrderItem orderItem = getOrderItem(cartItem, order);
                    orderItems.add(orderItem);
                }

                orderItemRepository.saveAll(orderItems);

                // Trừ điểm tích lũy (LoyaltyPoint) nếu có nhập
                if (orderRequest.getLoyaltyPoints() != null) {
                    user.get().setLoyaltyPoints(user.get().getLoyaltyPoints() - orderRequest.getLoyaltyPoints());
                    userRepository.save(user.get());
                }

                // Cập nhật số lượng tồn kho
                List<Inventory> inventories = new ArrayList<>();
                for (OrderItem orderItem: orderItems) {
                    Inventory inventory = orderItem.getProductVariant().getInventory();
                    inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity());
                    inventory.setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

                    // Nếu sau khi trừ số lượng tồn kho - sản phẩm còn số lượng 0 (tắt sản phẩm)
                    if (inventory.getQuantity() == 0) {
                        inventory.getProductVariant().setActive(false);
                    }
                    inventories.add(inventory);
                }
                inventoryRepository.saveAll(inventories);

                // Xóa giỏ hàng
                cartItemRepository.deleteAll(cartItems);
                cartRepository.delete(cart.get());
                break;

            // TH2. Thanh toán chuyển khoản VNPay (COD)
            case VNPAY:
                // Tạo mã orderId, orderCode để thanh toán
                String orderId = UUID.randomUUID().toString();
                String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
                String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
                String orderCode = "ORD-" + datePart + "-" + randomPart;
                
                // Tạo Order trước để thanh toán
                Order orderVNPay = new Order();
                orderVNPay.setCartId(cart.get().getCartId());
                orderVNPay.setOrderId(orderId);
                orderVNPay.setOrderCode(orderCode);
                orderVNPay.setUser(user.get());
                orderVNPay.setFullName(fullName);
                orderVNPay.setShippingAddress(shippingAddress);
                orderVNPay.setCoupon(coupon.orElse(null));
                orderVNPay.setCouponCode(coupon.map(Coupon::getCouponCode).orElse(null));
                orderVNPay.setDiscountPrice(coupon.map(Coupon::getDiscountPrice).orElse(null));
                orderVNPay.setLoyaltyPoints(orderRequest.getLoyaltyPoints());
                orderVNPay.setTotalQuantity(totalQuantity);
                orderVNPay.setTotalPrice(totalPrice);
                orderVNPay.setPaymentMethod(PaymentMethod.VNPAY);
                orderVNPay = orderRepository.save(orderVNPay);

                List<OrderItem> orderItemsVnPay = new ArrayList<>();
                for (CartItem cartItem2: cartItems) {
                    // Kiểm tra số lượng kho hiện tại để đặt hàng
                    if (cartItem2.getProductVariant().getInventory().getQuantity() < 1) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.builder()
                                        .status("error")
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Sản phẩm: " +
                                                cartItem2.getProductVariant().getVariantName() + " hết hàng"
                                        )
                                        .build());
                    }
                    OrderItem orderItem = getOrderItem(cartItem2, orderVNPay);

                    orderItemsVnPay.add(orderItem);
                }
                orderItemRepository.saveAll(orderItemsVnPay);

                ResponseEntity<ApiResponse<?>> response = paymentService.createUrlPayment(orderCode, (long) (double) totalPrice, request);
                if (response.getStatusCode().is2xxSuccessful()) {
                    // Lấy ApiResponse từ body
                    ApiResponse<?> apiResponse = response.getBody();

                    // Lấy paymentUrl từ data
                    String paymentUrl = (String) apiResponse.getData();
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(ApiResponse.builder()
                                    .status("success")
                                    .code(HttpStatus.CREATED.value())
                                    .message("Url thanh toán VNPay")
                                    .data(paymentUrl)
                                    .build());
                } else {
                    // Xóa Order
                    orderItemRepository.deleteAll(orderVNPay.getOrderItems());
                    orderRepository.delete(orderVNPay);

                    ApiResponse<?> apiResponse = response.getBody();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.builder()
                                    .status("error")
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Lỗi: " + apiResponse.getMessage())
                                    .build());
                }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .status("error")
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Phương thức thanh toán không hợp lệ")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(String orderCode, OrderStatus orderStatus) {
        // Kiểm tra order
        Optional<Order> order = orderRepository.findByOrderCode(orderCode);
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Đơn hàng không tồn tại: " + orderCode)
                            .build());
        }

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderStatus(orderStatus);
        orderHistory.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        orderHistory.setOrder(order.get());
        orderHistory = orderHistoryRepository.save(orderHistory);

        order.get().setCurrentStatus(orderHistory.getOrderStatus());
        orderRepository.save(order.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật trạng thái đơn hàng thành công")
                        .data(Map.of(
                                "orderHistoryId", orderHistory.getOrderHistoryId(),
                                "orderStatus", orderHistory.getOrderStatus(),
                                "createTime", orderHistory.getCreateTime()
                        ))
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllOrdersOfUser(int userId, int page, int size) {
        // Kiểm tra user có tồn tại
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Người dùng không tồn tại với ID: " + userId)
                            .build());
        }

        // Phân trang
        Pageable sortedPageable = PageRequest.of(
                page,
                size,
                Sort.by("createTime").descending()
        );

        Page<Order> orders = orderRepository.findAllOrdersByUser(userId, sortedPageable);

        // Chuyển sang Response
        Page<OrderResponse> orderResponses = orders
                .map(order -> OrderResponse.builder()
                        .orderId(order.getOrderId())
                        .orderCode(order.getOrderCode())
                        .fullName(order.getFullName())
                        .shippingAddress(order.getShippingAddress())
                        .couponCode(order.getCouponCode())
                        .discountPrice(order.getDiscountPrice())
                        .loyaltyPoints(order.getLoyaltyPoints())
                        .totalQuantity(order.getTotalQuantity())
                        .totalPrice(order.getTotalPrice())
                        .currentStatus(String.valueOf(order.getCurrentStatus()))
                        .paymentMethod(String.valueOf(order.getPaymentMethod()))
                        .createTime(order.getCreateTime())
                        .orderItemResponses(order.getOrderItems().stream()
                                .map(orderItem -> OrderItemResponse.builder()
                                        .orderItemId(orderItem.getOrderItemId())
                                        .productVariantName(orderItem.getProductVariantName())
                                        .productVariantImage(orderItem.getProductVariantImage())
                                        .price(orderItem.getPrice())
                                        .quantity(orderItem.getQuantity())
                                        .build())
                                .toList()
                        )
                        .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách đơn hàng của người dùng: " + user.get().getFullName())
                        .data(orderResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllOrders(int userId, int page, int size, LocalDateTime startTime, LocalDateTime endTime) {
        // Kiểm tra user có tồn tại
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Người dùng không tồn tại với ID: " + userId)
                            .build());
        }

        // Phân trang
        Pageable sortedPageable = PageRequest.of(
                page,
                size,
                Sort.by("createTime").descending()
        );

        // Nếu không nhập startTime, endTime -> Lấy ra tất cả Orders
        Page<Order> orders = null;
        if (startTime == null && endTime == null) {
            orders = orderRepository.findAll(sortedPageable);
        }

        orders = orderRepository.findAllOrdersByTime(startTime, endTime, sortedPageable);

        // Chuyển sang Response
        Page<OrderResponse> orderResponses = orders
                .map(order -> OrderResponse.builder()
                        .orderId(order.getOrderId())
                        .orderCode(order.getOrderCode())
                        .fullName(order.getFullName())
                        .shippingAddress(order.getShippingAddress())
                        .couponCode(order.getCouponCode())
                        .discountPrice(order.getDiscountPrice())
                        .loyaltyPoints(order.getLoyaltyPoints())
                        .totalQuantity(order.getTotalQuantity())
                        .totalPrice(order.getTotalPrice())
                        .currentStatus(String.valueOf(order.getCurrentStatus()))
                        .paymentMethod(String.valueOf(order.getPaymentMethod()))
                        .createTime(order.getCreateTime())
                        .orderItemResponses(order.getOrderItems().stream()
                                .map(orderItem -> OrderItemResponse.builder()
                                        .orderItemId(orderItem.getOrderItemId())
                                        .productVariantName(orderItem.getProductVariantName())
                                        .productVariantImage(orderItem.getProductVariantImage())
                                        .price(orderItem.getPrice())
                                        .quantity(orderItem.getQuantity())
                                        .build())
                                .toList()
                        )
                        .build());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách đơn hàng thành công")
                        .data(orderResponses)
                        .build());
    }

    private OrderItem getOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductVariant(cartItem.getProductVariant());
        orderItem.setProductVariantName(cartItem.getProductVariant().getVariantName());
        orderItem.setProductVariantImage(cartItem.getProductVariant().getVariantImageGroup().getImageVariants().get(0).getImagePath());
        orderItem.setPrice(cartItem.getProductVariant().getSellingPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }

    private String getFullAddress(List<Address> addresses) {
        // Lấy địa chỉ mặc định
        Address address = addresses.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElse(null);

        if (address == null) {
            return null;
        }

        return String.format("%s, %s, %s, %s",
                address.getAddressDetail(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince()
        );
    }
}
