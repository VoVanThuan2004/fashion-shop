package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_option_value", uniqueConstraints = {
        @UniqueConstraint(name = "uk_variant_option_value", columnNames = {"product_variant_id", "option_value_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductVariantOptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne
    @JoinColumn(name = "option_value_id")
    private ProductOptionValue optionValue;
}
