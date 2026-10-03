package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${school.avatar.dir.path}")
    private String avatarDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student with id = {}", studentId);
        Student student = studentService.findStudent(studentId);

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Uploaded file has no name");
        }

        logger.debug("Saving file '{}' ({} bytes, type = {}) for student {}",
                originalName, file.getSize(), file.getContentType(), studentId);

        Path filePath = Path.of(avatarDir, studentId + "." + getExtension(originalName));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        Avatar avatar = findAvatar(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImagePreview(filePath));

        avatarRepository.save(avatar);
        logger.debug("Avatar for student {} successfully saved, path = {}", studentId, filePath);
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.warn("Uploaded file is not a valid image, cannot generate preview");
                throw new IllegalArgumentException("File is not a valid image");
            }
            int height = (int) ((double) image.getHeight() / image.getWidth() * 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public Optional<Avatar> findAvatar(Long studentId) {
        logger.info("Was invoked method for find avatar by student id = {}", studentId);
        Optional<Avatar> avatarOpt = avatarRepository.findByStudentId(studentId);
        if (avatarOpt.isEmpty()) {
            logger.debug("Avatar not found for student {}", studentId);
        } else {
            logger.debug("Avatar found for student {}", studentId);
        }
        return avatarOpt;
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("File name has no extension: " + fileName);
        }
        return fileName.substring(dotIndex + 1);
    }


    public Page<Avatar> getAvatarsPage(Integer pageNumber, Integer pageSize) {
        logger.info("Was invoked method for get avatars page");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Avatar> page = avatarRepository.findAll(pageRequest);
        logger.debug("Fetched avatar page: number = {}, size = {}, totalElements = {}",
                pageNumber, pageSize, page.getTotalElements());
        return page;
    }
}
