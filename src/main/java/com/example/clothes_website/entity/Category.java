package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    private String categoryName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Self reference (mối quan hệ đệ quy)
    // Nhiều category con thuộc về 1 category cha
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category categoryParent;

    // 1 category cha có nhiều category con
    @OneToMany(mappedBy = "categoryParent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categoryChildren;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

}
