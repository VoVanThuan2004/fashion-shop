package com.example.clothes_website.controller;

import com.example.clothes_website.dto.request.AddressRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.security.CustomUserDetails;
import com.example.clothes_website.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/api/v1/address")
    public ResponseEntity<ApiResponse<?>> getAllAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return addressService.getAllAddresses(userId);
    }

    @PostMapping("/api/v1/address")
    public ResponseEntity<ApiResponse<?>> addAddress(@RequestBody AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return addressService.addAddress(addressRequest, userId);
    }

    @DeleteMapping("/api/v1/address/{addressId}")
    public ResponseEntity<ApiResponse<?>> deleteAddress(@PathVariable int addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return addressService.deleteAddress(addressId, userId);
    }

    @PutMapping("/api/v1/address/{addressId}")
    public ResponseEntity<ApiResponse<?>> updateAddress(@PathVariable int addressId, @RequestBody AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return addressService.updateAddress(addressId, userId, addressRequest);
    }

    // Thiết lập địa chỉ mặc định
    @PutMapping("/api/v1/address/default/{addressId}")
    public ResponseEntity<ApiResponse<?>> updateDefaultAddress(@PathVariable int addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = customUserDetails.getUserId();

        return addressService.updateDefaultAddress(addressId, userId);
    }

}
