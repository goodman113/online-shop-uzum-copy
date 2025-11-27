package project.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import project.model.Image;
import project.repository.repository.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageService {
    final ImageRepository repository;
    public Image upload(MultipartFile file) {
        try {

            // Get project root (directory where src/ and uploads/ exist)
            String rootPath = System.getProperty("user.dir");

            // Target uploads folder
            Path uploadDir = Paths.get(rootPath, "uploads");

            Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "." +
                    StringUtils.getFilenameExtension(file.getOriginalFilename());

            Path targetPath = uploadDir.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Image image = new Image();
            image.setName(fileName);
            image.setOriginalName(file.getOriginalFilename());
            image.setSize(file.getSize());
            image.setPath("/uploads/" + fileName);
            image.setContentType(file.getContentType());

            repository.save(image);

            return image;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

}
