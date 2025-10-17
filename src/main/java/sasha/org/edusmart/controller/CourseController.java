package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.dto.CourseDTO;
import sasha.org.edusmart.dto.FileResourceDTO;
import sasha.org.edusmart.dto.ProfessorDTO;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.service.CourseService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController{

    private final CourseService courseService;

    public  CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //ADMIN
    @PostMapping(value = "/admin/createCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return createdCourse != null
                ? ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create course");
    }

    //ADMIN
    @DeleteMapping("/admin/deleteCourse/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteCourse(@PathVariable("title") String title) {
        boolean deleted = courseService.deleteCourse(title);
        return deleted
                ? ResponseEntity.ok(true)  // Deleted successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Course not found to delete
    }


    @GetMapping("/getAllCourses")
    public List<CourseDTO> getAllCourses() {
        List<CourseDTO> courseDTOS = courseService.getAllCourses();
        if (courseDTOS.isEmpty()) {
            return new ArrayList<>();
        }
        return courseDTOS; // Return all course list
    }

    @GetMapping("/getCourseByTitle")
    public CourseDTO getCourseByTitle(@RequestParam String title) {
        CourseDTO courseDTO = courseService.getCourseByTitle(title);
        return courseDTO; // Return course
    }

    @GetMapping("/getLectureByCourseTitleAndIndex")
    public String getLectureByCourseTitleAndIndex(@RequestParam String title, Integer lectureIndex) {
        String LectureByCourseTitleAndIndex = courseService.getLectureByCourseTitleAndIndex(title, lectureIndex);
        return LectureByCourseTitleAndIndex; // Return Lecture By CourseTitle And Index
    }

    @GetMapping("/getStudentsByCourseTitle")
    public List<StudentDTO> getStudentsByCourseTitle(@RequestParam String title) {
        List<StudentDTO> studentsByCourse = courseService.getStudentsByCourseTitle(title);
        return studentsByCourse; // Return students By Course
    }

    @GetMapping("/getProfessorByCourseTitle")
    public ResponseEntity<?> getProfessorByCourseTitle(@RequestParam String title) {
        ProfessorDTO professorDTO = courseService.getProfessorByCourseTitle(title);
        if (professorDTO != null) {
            return ResponseEntity.ok(professorDTO);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/getFilesByCourseTitle")
    public List<FileResourceDTO> getFilesByCourseTitle(@RequestParam String title) {
        List<FileResourceDTO> filesByCourseDTOS = courseService.getFilesByCourseTitle(title);
        return filesByCourseDTOS; // Return files By Course
    }
}