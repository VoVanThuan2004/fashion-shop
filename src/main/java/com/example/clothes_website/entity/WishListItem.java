package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wishlistItemId;

    @ManyToOne
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime addedTime;
}
