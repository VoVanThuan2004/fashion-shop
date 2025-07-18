package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ImageVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageVariantId;

    private String imagePath;
    private String publicId;       // Cloudinary public_id

    @ManyToOne
    @JoinColumn(name = "group_id")
    private VariantImageGroup variantImageGroup;
}
