package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageMessageId;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Messages message;
}
