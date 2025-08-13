package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
