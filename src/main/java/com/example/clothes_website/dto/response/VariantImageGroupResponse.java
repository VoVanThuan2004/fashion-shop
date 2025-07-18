package com.example.clothes_website.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class VariantImageGroupResponse {
    private int groupId;
    private int productId;
    private int attributeValueId;
    private List<String> imagesPath;
}
