package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, Integer> {

    @Query("select u from Users u join u.userRoles ur where ur.role.roleName = 'USER' order by u.createTime desc")
    Page<Users> findCustomers(Pageable pageable);

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = 'ADMIN'")
    Optional<Users> findAdmin();


    @Query("""
        select count(u) from Users u
        join u.userRoles ur
        join ur.role r
        where r.roleName <> 'ADMIN'
    """)
    Integer getTotalUsers();  // Lấy tổng người dùng hiện tại (trừ ADMIN)

    @Query("""
        select count(u) from Users u
        join u.userRoles ur
        join ur.role r
        where r.roleName <> 'ADMIN'
        and u.createTime >= :startTime and u.createTime < :endTime
    """)
    Integer getTotalNewUsers
            (@Param("startTime") LocalDateTime startTime, // Lấy tổng người dùng mới (trừ ADMIN)
             @Param("endTime") LocalDateTime endTime
            );
}
