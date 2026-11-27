package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_media", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_variant_media", columnNames = {"product_variant_id", "media_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductVariantMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private ProductMedia media;

    private int position;
    private boolean isFeatured;
}
