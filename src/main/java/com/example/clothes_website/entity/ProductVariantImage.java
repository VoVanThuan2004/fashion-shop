package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_image")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductVariantImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private String url;
    private String publicId;
    private int position;
    private boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;
}
