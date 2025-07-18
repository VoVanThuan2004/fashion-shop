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
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attributeValueId;

    private String value;
    private String hexCode;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VariantImageGroup> variantImageGroups;
}
