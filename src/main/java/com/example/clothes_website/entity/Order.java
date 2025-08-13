package com.example.clothes_website.entity;

import com.example.clothes_website.enums.OrderStatus;
import com.example.clothes_website.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_orderCode", columnList = "order_code"),
        @Index(name = "idx_orders_userId", columnList = "user_id")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Order {
    @Id
    private String orderId;

    private String orderCode;

    // Thông tin người dùng
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;
    private String fullName;
    private String shippingAddress;

    // Mã giảm giá
    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;
    private String couponCode;
    private Double discountPrice;

    private Integer loyaltyPoints;
    private Integer totalQuantity;
    private Double totalPrice;

    private Integer cartId; // Liên kết để truy vấn

    // Thanh toán
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Trạng thái hiện tại
    private OrderStatus currentStatus;

    // Thời gian đặt hàng
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderHistory> orderHistories = new ArrayList<>();


    @PrePersist
    public void generateIds() {
        if (this.orderId == null) {
            // Sinh UUID cho DB
            this.orderId = UUID.randomUUID().toString();
        }
        if (this.orderCode == null) {
            // Ngày hiện tại dạng yyyyMMdd
            String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            // Lấy 6 ký tự đầu từ UUID để rút gọn
            String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            this.orderCode = "ORD-" + datePart + "-" + randomPart;
        }
    }
}
