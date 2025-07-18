package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AddressRequest;
import com.example.clothes_website.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<ApiResponse<?>> getAllAddresses(int userId);

    ResponseEntity<ApiResponse<?>> addAddress(AddressRequest addressRequest, int userId);

    ResponseEntity<ApiResponse<?>> deleteAddress(int addressId, int userId);

    ResponseEntity<ApiResponse<?>> updateAddress(int addressId, int userId, AddressRequest addressRequest);

    ResponseEntity<ApiResponse<?>> updateDefaultAddress(int addressId, int userId);
}
