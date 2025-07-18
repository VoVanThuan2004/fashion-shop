package com.example.clothes_website.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AddressRequest {
    private String wardCode;
    private String ward;
    private String districtCode;
    private String district;
    private String provinceCode;
    private String province;

    private String addressDetail;
}
