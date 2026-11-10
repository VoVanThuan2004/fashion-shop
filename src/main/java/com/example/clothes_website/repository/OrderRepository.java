package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
        select order from Order order
        where order.orderCode = :orderCode
    """)
    Optional<Order> findByOrderCode(@Param("orderCode") String orderCode);

    @Query("""
        select order from Order order
        where order.user.userId = :userId
    """)
    Page<Order> findAllOrdersByUser(@Param("userId") int userId, Pageable pageable);

    @Query("""
        select count(order) from Order order
    """)
    Integer getTotalOrders();

    // Tính tổng doanh thu từ các orders
    @Query("""
        select sum(order.totalPrice) from Order order
    """)
    Double getTotalRevenue();

    // Lấy tổng đơn hàng theo thời gian
    @Query("""
        select order from Order order
        where order.createTime >= :startTime and order.createTime <= :endTime
    """)
    Page<Order> findAllOrdersByTime(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, Pageable pageable);


    /*
        Thống kê
    */

    // 1. Thống kê theo tháng

    @Query("""
        SELECT 
            MONTH(o.createTime) AS month,
            COUNT(o.orderId) AS totalOrders,
            SUM(o.totalPrice) AS totalRevenue,
            SUM((pv.sellingPrice - pv.originalPrice) * oi.quantity) AS totalProfit 
        FROM Order o
        JOIN OrderItem oi ON o.orderId = oi.order.orderId
        JOIN ProductVariant pv ON oi.productVariant.productVariantId = pv.productVariantId
        WHERE FUNCTION('YEAR', o.createTime) = :year
        GROUP BY MONTH(o.createTime)
        ORDER BY MONTH(o.createTime)
    """)
    List<Object[]> getMonthlyStatistics(@Param("year") int year);

    // 2. Thống kê theo quý
    @Query("""
        SELECT 
            FLOOR((MONTH(o.createTime) - 1) / 3) AS quarter,
            COUNT(o.orderId) AS totalOrders,
            SUM(o.totalPrice) AS totalRevenue,
            SUM((pv.sellingPrice - pv.originalPrice) * oi.quantity) AS totalProfit 
        FROM Order o
        JOIN OrderItem oi ON o.orderId = oi.order.orderId
        JOIN ProductVariant pv ON oi.productVariant.productVariantId = pv.productVariantId
        WHERE FUNCTION('YEAR', o.createTime) = :year
        GROUP BY FLOOR((MONTH(o.createTime) - 1) / 3)
        ORDER BY FLOOR((MONTH(o.createTime) - 1) / 3)
    """)
    List<Object[]> getQuarterlyStatistics(@Param("year") int year);

    // 3. Thống kê theo tuần
    @Query("""
        SELECT 
            FUNCTION('DAYOFWEEK', o.createTime) AS dayOfWeek,
            COUNT(o.orderId) AS totalOrders,
            SUM(o.totalPrice) AS totalRevenue,
            SUM((pv.sellingPrice - pv.originalPrice) * oi.quantity) AS totalProfit 
        FROM Order o
        JOIN OrderItem oi ON o.orderId = oi.order.orderId
        JOIN ProductVariant pv ON oi.productVariant.productVariantId = pv.productVariantId
        WHERE o.createTime >= :startTime AND o.createTime < :endTime
        GROUP BY FUNCTION('DAYOFWEEK', o.createTime)
        ORDER BY FUNCTION('DAYOFWEEK', o.createTime)
    """)
    List<Object[]> getWeeklyStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
