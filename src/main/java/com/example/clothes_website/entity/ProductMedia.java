package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_media", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_media_public_id", columnNames = {"product_id", "public_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mediaId;

    private String url;
    private String publicId;
    private String mediaType;
    private String altText;
    private int position;
    private boolean isFeatured;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "media", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariantMedia> variantAssociations = new ArrayList<>();
}
