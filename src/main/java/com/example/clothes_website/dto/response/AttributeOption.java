package com.example.clothes_website.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeOption {
    private String value;
    private List<Integer> variantIds;
}
