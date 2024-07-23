package com.example.praksa.Controllers;

import com.example.praksa.Services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @RequestMapping(method = RequestMethod.POST , consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')" )
    public ResponseEntity<?> uploadImage(@RequestPart("image")  MultipartFile file) throws IOException {
        String uploadImage = imageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

    @RequestMapping(method = RequestMethod.GET , value = "getImage")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')" )
    public ResponseEntity<?> downloadImage(@RequestParam Long imageId) {
        byte[] imageData = imageService.downloadImage(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
                .body(imageData);
    }
}
