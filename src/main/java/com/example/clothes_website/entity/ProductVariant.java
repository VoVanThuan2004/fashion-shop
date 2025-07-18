package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
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
}
