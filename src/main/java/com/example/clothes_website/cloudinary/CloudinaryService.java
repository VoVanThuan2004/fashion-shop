package com.example.clothes_website.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final String upload_preset;

    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloud_name,
                             @Value("${cloudinary.api_key}") String api_key,
                             @Value("${cloudinary.api_secret}") String api_secret,
                             @Value("${cloudinary.upload_preset}") String upload_preset
    )
    {
        this.upload_preset = upload_preset;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloud_name,
                "api_key", api_key,
                "api_secret", api_secret
        )
        );
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "upload_preset", upload_preset
        ));

        return uploadResult.get("secure_url").toString();
    }

    public UploadResult uploadCloudinaryImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "upload_preset", upload_preset
        ));
        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        return new UploadResult(url, publicId);
    }

    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            return false;
        }
    }

    // upload file (ảnh, pdf,...)
    public UploadResult uploadFile (MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String resourceType;

        if (contentType == null) {
            throw new IllegalArgumentException("Không xác định được loại file");
        }

        if (contentType.startsWith("image/")) {
            resourceType = "image";
        } else if (contentType.startsWith("video/")) {
            resourceType = "video";
        } else {
            resourceType = "raw";
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", resourceType,
                "upload_preset", upload_preset
        ));

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        return new UploadResult(url, publicId);
    }
}
