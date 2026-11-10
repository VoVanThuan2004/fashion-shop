package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @CreationTimestamp
    private Instant createTime;

    @UpdateTimestamp
    @Column(updatable = false)
    private Instant updateTime;

    @Builder.Default
    private boolean deleted = false;

    // Self reference (mối quan hệ đệ quy)
    // Nhiều category con thuộc về 1 category cha
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category categoryParent;

    // 1 category cha có nhiều category con
    @OneToMany(mappedBy = "categoryParent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categoryChildren = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}
