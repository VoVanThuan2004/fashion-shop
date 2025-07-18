package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class VariantAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int variantAttributeId;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne
    @JoinColumn(name = "attribute_value_id")
    private AttributeValue attributeValue;

}
