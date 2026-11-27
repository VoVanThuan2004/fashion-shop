package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_option_value", uniqueConstraints = {
        @UniqueConstraint(name = "uk_option_value", columnNames = {"option_id", "value"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductOptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int optionValueId;

    private String value;
    private String hexCode;
    private int position;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private ProductOption option;
}
