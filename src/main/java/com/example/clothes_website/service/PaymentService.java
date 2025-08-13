package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.entity.*;
import com.example.clothes_website.enums.OrderStatus;
import com.example.clothes_website.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    private final OrderRepository orderRepository;


    public ResponseEntity<ApiResponse<?>> createUrlPayment(String orderCode, Long amount, HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            String clientIp = "127.0.0.1"; // fallback IP

            if (request != null) {
                clientIp = request.getRemoteAddr(); // Lấy IP thật nếu có
            }

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            String vnp_IpAddr = clientIp;
            String vnp_OrderInfo = "Thanh toán đơn hàng: " + orderCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            amount = amount * 100;
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", orderCode);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Date dt = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(dt);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Thêm vnp_ExpireDate (hết hạn sau 15 phút)
            Calendar cld = Calendar.getInstance();
            cld.setTime(dt);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);


            // Sort parameters
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            List<String> hashDataList = new ArrayList<>();
            List<String> queryList = new ArrayList<>();

            for (String fieldName : fieldNames) {
                String value = vnp_Params.get(fieldName);
                if (value != null && !value.isEmpty()) {
                    hashDataList.add(fieldName + "=" + URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                    queryList.add(fieldName + "=" + URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                }
            }

            String hashData = String.join("&", hashDataList);
            String query = String.join("&", queryList);
            String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData);
            query += "&vnp_SecureHash=" + vnp_SecureHash;

            String paymentUrl = vnp_PayUrl + "?" + query;

            return ResponseEntity.status(201)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(201)
                            .message("Tạo url thanh toán thành công")
                            .data(paymentUrl)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(500)
                            .message(e.getMessage())
                            .build());
        }
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating HMAC SHA512", ex);
        }
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public ResponseEntity<ApiResponse<?>> returnPayment(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String orderCode = request.getParameter("vnp_TxnRef");

        // Tìm Order theo orderCode
        Optional<Order> order = orderRepository.findByOrderCode(orderCode);
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Đơn hàng không tồn tại: " + orderCode)
                            .build());
        }

        if ("00".equals(responseCode)) {
            order.get().setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            order.get().setCurrentStatus(OrderStatus.PAID);
            order = Optional.of(orderRepository.save(order.get()));

            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(order.get());
            orderHistory.setCreateTime(order.get().getCreateTime());
            orderHistory.setOrderStatus(OrderStatus.PAID);
            orderHistoryRepository.save(orderHistory);

            // Trừ điểm tích lũy (LoyaltyPoint) nếu có nhập
            if (order.get().getLoyaltyPoints() != null) {
                Users user = order.get().getUser();
                user.setLoyaltyPoints(user.getLoyaltyPoints() - order.get().getLoyaltyPoints());
                userRepository.save(user);
            }

            // Cập nhật số lượng tồn kho
            List<Inventory> inventories = new ArrayList<>();
            for (OrderItem orderItem: order.get().getOrderItems()) {
                Inventory inventory = orderItem.getProductVariant().getInventory();
                inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity());
                inventory.setUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                inventories.add(inventory);
            }
            inventoryRepository.saveAll(inventories);

            // Xóa giỏ hàng
            Optional<Cart> cart = cartRepository.findById(order.get().getCartId());
            if (cart.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status("error")
                                .code(HttpStatus.NOT_FOUND.value())
                                .message("Giỏ hàng không tồn tại với ID: " + order.get().getOrderId())
                                .build());
            }

            cartItemRepository.deleteAll(cart.get().getCartItems());
            cartRepository.delete(cart.get());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.builder()
                            .status("success")
                            .code(200)
                            .message("Thanh toán thành công")
                            .build());

        }
        else {
            // Thanh toán bị lỗi - không thành công
            // 1. Xóa đơn hàng đã lưu
            orderItemRepository.deleteAll(order.get().getOrderItems());
            orderRepository.delete(order.get());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(400)
                            .message("Thanh toán thất bại")
                            .build());
        }

    }
}
