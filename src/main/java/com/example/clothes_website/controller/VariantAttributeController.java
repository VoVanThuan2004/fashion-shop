package com.example.clothes_website.controller;

import com.example.clothes_website.service.VariantAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VariantAttributeController {
    private final VariantAttributeService variantAttributeService;
}
