package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;

    private String wardCode;
    private String ward;
    private String districtCode;
    private String district;
    private String provinceCode;
    private String province;

    private String addressDetail;
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
