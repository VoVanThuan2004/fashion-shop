package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_option", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_option_name", columnNames = {"product_id", "name"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int optionId;

    private String name;
    private int position;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductOptionValue> values = new ArrayList<>();
}
