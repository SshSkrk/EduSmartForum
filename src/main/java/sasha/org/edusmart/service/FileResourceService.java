package sasha.org.edusmart.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sasha.org.edusmart.model.Course;
import sasha.org.edusmart.model.FileResource;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.FileResourceRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
public class FileResourceService {

    private final FileResourceRepository fileResourceRepository;
    private final CourseRepository courseRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public FileResourceService(FileResourceRepository fileResourceRepository,
                               CourseRepository courseRepository) {
        this.fileResourceRepository = fileResourceRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public boolean uploadFileOnCourse(String courseTitle, MultipartFile multipartFile) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(courseTitle);

        if (courseOptional.isEmpty()) {
            return false;
        }

        try {
            Course course = courseOptional.get();

            // Ensure upload folder exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IOException("Invalid file name");
            }

            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);

            // Save file on disk
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create and persist FileResource entity
            FileResource fileResource = new FileResource();
            fileResource.setFileName(originalFilename);
            fileResource.setFilePath(filePath.toString());
            fileResource.setCourse(course);
            fileResourceRepository.save(fileResource);

            // Associate file with course
            course.getFiles().add(fileResource);
            courseRepository.save(course);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Resource downloadFile(Integer fileId) throws IOException {
        FileResource fileResource = fileResourceRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        Path filePath = Paths.get(fileResource.getFilePath());

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found on server: " + fileResource.getFileName());
        }

        return new UrlResource(filePath.toUri());
    }
}
