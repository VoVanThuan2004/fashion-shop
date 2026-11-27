package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variant", uniqueConstraints = {
        @UniqueConstraint(name = "uk_variant_option_signature", columnNames = {"product_id", "option_signature"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productVariantId;

    private String variantName;
    private double originalPrice;
    private double sellingPrice;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private boolean isActive;

    @Column(unique = true)
    private String sku;
    private String optionSignature;

    @ManyToOne
    @JoinColumn(name = "image_group_id")
    private VariantImageGroup variantImageGroup;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private Inventory inventory;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    private List<VariantAttribute> variantAttributes;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariantOptionValue> variantOptionValues = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariantImage> variantImages = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariantMedia> mediaAssociations = new ArrayList<>();

    public String getPrimaryImagePath() {
        if (variantImages != null && !variantImages.isEmpty()) {
            return variantImages.stream()
                    .filter(ProductVariantImage::isPrimary)
                    .map(ProductVariantImage::getUrl)
                    .findFirst()
                    .orElse(variantImages.get(0).getUrl());
        }
        return product != null ? product.getImagePath() : null;
    }
}
