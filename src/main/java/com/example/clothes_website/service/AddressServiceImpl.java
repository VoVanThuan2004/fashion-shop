package com.example.clothes_website.service;

import com.example.clothes_website.dto.request.AddressRequest;
import com.example.clothes_website.dto.response.AddressResponse;
import com.example.clothes_website.dto.response.ApiResponse;
import com.example.clothes_website.entity.Address;
import com.example.clothes_website.entity.Users;
import com.example.clothes_website.repository.AddressRepository;
import com.example.clothes_website.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> getAllAddresses(int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        List<Address> addresses = user.get().getAddresses();
        List<AddressResponse> addressResponses = addresses.stream()
                .map(address -> AddressResponse.builder()
                        .addressId(address.getAddressId())
                        .wardCode(address.getWardCode())
                        .ward(address.getWard())
                        .provinceCode(address.getProvinceCode())
                        .province(address.getProvince())
                        .district(address.getDistrict())
                        .districtCode(address.getDistrictCode())
                        .addressDetail(address.getAddressDetail())
                        .isDefault(address.isDefault())
                        .build())
                .toList();


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Fetch addresses successfully")
                        .data(addressResponses)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addAddress(AddressRequest addressRequest, int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        // Check nếu đây là địa chỉ đầu tiên -> default: true, ngược lại là false
        // Thêm địa chỉ
        Address address = Address.builder()
                .wardCode(addressRequest.getWardCode())
                .ward(addressRequest.getWard())
                .districtCode(addressRequest.getDistrictCode())
                .district(addressRequest.getDistrict())
                .provinceCode(addressRequest.getProvinceCode())
                .province(addressRequest.getProvince())
                .addressDetail(addressRequest.getAddressDetail())
                .isDefault(user.get().getAddresses().isEmpty())
                .user(user.get())
                .build();

        addressRepository.save(address);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(201)
                        .message("Address added successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteAddress(int addressId, int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Address not found")
                            .build());
        }

        // Xóa theo id
        addressRepository.delete(address.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Address deleted successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateAddress(int addressId, int userId, AddressRequest addressRequest) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        // Cập nhật address theo id
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Address not found")
                            .build());
        }

        address.get().setWardCode(addressRequest.getWardCode());
        address.get().setWard(addressRequest.getWard());
        address.get().setDistrictCode(addressRequest.getDistrictCode());
        address.get().setDistrict(addressRequest.getDistrict());
        address.get().setProvinceCode(addressRequest.getProvinceCode());
        address.get().setProvince(addressRequest.getProvince());
        address.get().setAddressDetail(addressRequest.getAddressDetail());
        addressRepository.save(address.get());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Address updated successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> updateDefaultAddress(int addressId, int userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("User not found")
                            .data(null)
                            .build());
        }

        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status("error")
                            .code(404)
                            .message("Address is not found")
                            .build());
        }

        // Duyệt xem addressId tồn tại trong user -> set địa chỉ mặc định
        for (int i = 0; i < user.get().getAddresses().size(); i++) {
            if (user.get().getAddresses().get(i).getAddressId() == addressId) {
                user.get().getAddresses().get(i).setDefault(true);
            }
            else {
                user.get().getAddresses().get(i).setDefault(false);
            }
        }

        addressRepository.saveAll(user.get().getAddresses());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.builder()
                        .status("success")
                        .code(200)
                        .message("Default address updated successfully")
                        .build());
    }
}
