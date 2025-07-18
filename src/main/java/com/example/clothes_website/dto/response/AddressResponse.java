package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressResponse {
    private int addressId;

    private String wardCode;
    private String ward;
    private String districtCode;
    private String district;
    private String provinceCode;
    private String province;

    private String addressDetail;
    private boolean isDefault;
}
