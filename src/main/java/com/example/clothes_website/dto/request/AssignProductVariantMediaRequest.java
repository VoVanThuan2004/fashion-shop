package com.example.clothes_website.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignProductVariantMediaRequest {
    @Schema(
            description = "Danh sách media thuộc product cha, thứ tự phần tử là thứ tự hiển thị",
            example = "[12, 15, 18]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Danh sách media không được để trống")
    private List<@Positive(message = "Media ID phải là số dương") Integer> mediaIds;

    @Schema(
            description = "Media đại diện của variant; phải nằm trong mediaIds",
            example = "15",
            nullable = true
    )
    @Positive(message = "Featured media ID phải là số dương")
    private Integer featuredMediaId;
}
