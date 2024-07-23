package com.example.praksa.Services;

import com.example.praksa.Models.Image;
import com.example.praksa.Repositories.ImageRepository;
import com.example.praksa.Utills.ImageUtills;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    public String uploadImage(MultipartFile imageFile) throws IOException {
        var imageToSave = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtills.compressImage(imageFile.getBytes()))
                .build();
        imageRepository.save(imageToSave);
        return "file uploaded successfully : " + imageFile.getOriginalFilename();
    }

    public Image addImage(MultipartFile imageFile) throws IOException {
        var imageToSave = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtills.compressImage(imageFile.getBytes()))
                .build();
        imageRepository.save(imageToSave);
        return imageToSave;
    }

    @Transactional
    public byte[] downloadImage(Long imageId) {
        Optional<Image> dbImage = imageRepository.findById(imageId);

        return dbImage.map(image -> {
            try {
                return ImageUtills.decompressImage(image.getImageData());
            } catch (DataFormatException | IOException exception) {
                throw new ContextedRuntimeException("Error downloading an image", exception)
                        .addContextValue("Image ID",  image.getId())
                        .addContextValue("Image name", image.getName());
            }
        }).orElse(null);
    }
    @Transactional
    public String getImageName(Long imageId){
        Optional<Image> dbImage = imageRepository.findById(imageId);
        return dbImage.get().getName();
    }
}
