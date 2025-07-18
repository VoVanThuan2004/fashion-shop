package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int inventoryId;

    private int quantity;

    @Transient      // Trường tính toán, không lưu DB
    private boolean isInStock() {
        return this.quantity > 0;
    }

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @OneToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;
}
