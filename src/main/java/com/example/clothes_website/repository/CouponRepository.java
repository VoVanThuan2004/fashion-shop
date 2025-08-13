package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Query("""
        select c from Coupon c
        order by c.expiredTime desc
    """)
    List<Coupon> findAllCoupons();
}
