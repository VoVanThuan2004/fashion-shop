package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
