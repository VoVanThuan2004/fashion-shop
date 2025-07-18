package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brandId;

    private String brandName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Product> products;
}
