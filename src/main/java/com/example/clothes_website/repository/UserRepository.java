package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, Integer> {

    @Query("select u from Users u join u.userRoles ur where ur.role.roleName = 'USER' order by u.createTime desc")
    Page<Users> findCustomers(Pageable pageable);

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = 'ADMIN'")
    Optional<Users> findAdmin();
}
