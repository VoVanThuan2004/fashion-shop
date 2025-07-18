package com.example.clothes_website.repository;

import com.example.clothes_website.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findFirstByUser_UserId(int userId);

    @Query("select u from UserRole u where u.user.userId = :userId and u.role.roleName = :ADMIN")
    Optional<UserRole> findByUserIdAndRoleName(@Param("userId") int userId);
}
