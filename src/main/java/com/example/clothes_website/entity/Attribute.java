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
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attributeId;

    private String name;
    private String code;
    private String type;
    private int priority;

    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY)
    private List<AttributeValue> attributeValues;
}
