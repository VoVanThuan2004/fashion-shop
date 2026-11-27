package com.example.clothes_website.service;

import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.dto.request.BrandRequest;
import com.example.clothes_website.dto.response.BrandResponse;
import com.example.clothes_website.entity.Brand;
import com.example.clothes_website.exception.BadRequestException;
import com.example.clothes_website.exception.ResourceNotFoundException;
import com.example.clothes_website.mapper.BrandMapper;
import com.example.clothes_website.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<BrandResponse> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();

        return brandMapper.mapToResponseList(brands);
    }

    @Override
    public BrandResponse addBrand(BrandRequest brandRequest) {
        // Validate tên thương hiệu có hợp lệ hay không
        if (brandRepository.existsByBrandNameIgnoreCaseAndIsDeletedFalse(brandRequest.getBrandName().trim())) {
            throw new BadRequestException("Tên thương hiệu đã tồn tại");
        }

        Brand brand = new Brand();
        brand.setBrandName(brandRequest.getBrandName());
        brandRepository.save(brand);

        return brandMapper.mapToResponse(brand);
    }

    @Override
    public BrandResponse updateBrand(int brandId, BrandRequest brandRequest) {
        Brand brand = getBrandById(brandId);

        // Validate tên thương hiệu có hợp lệ hay không
        if (brandRepository.existsByBrandNameIgnoreCaseAndIsDeletedFalse(brandRequest.getBrandName())) {
            throw new BadRequestException("Tên thương hiệu đã tồn tại");
        }

        brand.setBrandName(brandRequest.getBrandName());
        brandRepository.save(brand);

        return brandMapper.mapToResponse(brand);
    }

    @Override
    public void deleteBrand(int brandId) {
        Brand brand = getBrandById(brandId);

        brand.setIsDeleted(true);
        brandRepository.save(brand);
    }

    private Brand getBrandById(int brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu không tồn tại"));
    }

}
