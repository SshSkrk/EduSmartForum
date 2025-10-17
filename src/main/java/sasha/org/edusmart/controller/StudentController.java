package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.model.Person;
import sasha.org.edusmart.service.StudentService;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    //STUDENT
    @PostMapping(value = "/student/enrollStudentOnCourse/{studentId}/{courseTitle}")
    public ResponseEntity<?> enrollStudentOnCourse( @PathVariable("studentId") Integer studentId,
                                                    @PathVariable("courseTitle") String courseTitle) {
        boolean enrolled = studentService.enrollStudentOnCourse(studentId, courseTitle);
        return enrolled
                ? ResponseEntity.status(HttpStatus.CREATED).body("Student enrolled successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to enrolled student");
    }

    //STUDENT
    @GetMapping(value = "/student/isEnrolledOnCourse/{studentId}/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public boolean isEnrolledOnCourse( @PathVariable("studentId") Integer studentId,
                                                    @PathVariable("courseId") Integer courseId) {
        boolean enrolled = studentService.isEnrolledOnCourse(studentId, courseId);
        return enrolled;
    }

    //STUDENT
    @PostMapping(value = "/student/markLectureAsRead/{studentId}/{lectureIndex}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> markLectureAsRead(@PathVariable("studentId") Integer studentId,
                                               @PathVariable("lectureIndex") Integer lectureIndex) {
        boolean marked = studentService.markLectureAsRead(studentId, lectureIndex);

        return marked
                ? ResponseEntity.status(HttpStatus.CREATED).body("Lecture marked successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to marked lecture");
    }

    //STUDENT
    @GetMapping(value = "/student/isLectureRead/{studentId}/{lectureIndex}")
    @PreAuthorize("hasRole('STUDENT')")
    public boolean isLectureRead( @PathVariable("studentId") Integer studentId,
                                  @PathVariable("lectureIndex") Integer lectureIndex) {
        boolean enrolled = studentService.isLectureRead(studentId, lectureIndex);
        return enrolled;
    }

    @GetMapping("/student/courseTitle")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> getStudentCourseTitle(Authentication authentication) {
        // Get email from authentication
        String email = authentication.getName();

        StudentDTO studentDTO = studentService.findByEmail(email);
        if (studentDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(studentDTO.getCourseTitle());
    }

}
