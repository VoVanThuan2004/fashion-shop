package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    private String roleName;
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> roleUsers;
}
