package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class VariantImageGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_value_id")
    private AttributeValue attributeValue;

    @OneToMany(mappedBy = "variantImageGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageVariant> imageVariants;

    @OneToMany(mappedBy = "variantImageGroup", fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants;
}
