package com.example.clothes_website.service;

import com.example.clothes_website.repository.VariantAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VariantAttributeServiceImpl implements VariantAttributeService {
    private final VariantAttributeRepository variantAttributeRepository;
}
