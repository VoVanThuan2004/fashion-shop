package com.example.clothes_website.service;


import com.example.clothes_website.dto.response.WishListResponse;
import org.springframework.data.domain.Page;

public interface WishListService {
    void addToWishList(Integer productId, int userId);

    void removeFromWishList(Integer productId, int userId);

    Page<WishListResponse> getWishLists(int userId, int page, int size);

    Boolean checkProductInWishList(Integer productId, int userId);

    void clearMyWishList(int userId);
}
