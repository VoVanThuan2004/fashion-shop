package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wishListId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
