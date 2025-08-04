package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "cart_item", indexes = {
        @Index(name = "idx_cartitem_product_variant_id", columnList = "productVariantId"),
        @Index(name = "idx_cartitem_cart_id", columnList = "cartId")
})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;

    @ManyToOne
    @JoinColumn(name = "productVariantId")
    private ProductVariant productVariant;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;
}
