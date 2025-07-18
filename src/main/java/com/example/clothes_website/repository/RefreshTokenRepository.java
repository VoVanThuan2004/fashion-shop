package com.example.clothes_website.repository;

import com.example.clothes_website.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Query("select re from RefreshToken re where re.refreshToken = :refreshToken")
    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
