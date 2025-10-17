package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.model.Course;
import sasha.org.edusmart.model.Person;
import sasha.org.edusmart.model.Student;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public StudentDTO findByEmail(String email) {
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            StudentDTO studentDTO = student.studentDTO();
            return studentDTO;
        }
        return null;
    }

    //Student
    @Transactional
    public boolean enrollStudentOnCourse(Integer studentId, String courseTitle) {
        Optional<Student> studentOptional = studentRepository.getStudentById(studentId);
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(courseTitle);
        if (studentOptional.isPresent() && courseOptional.isPresent()) {
            Student student = studentOptional.get();
            Course course = courseOptional.get();
            if (student.getCourse() == null && !course.getStudents().contains(student)) {
                student.setCourse(course);
                course.getStudents().add(student);
                studentRepository.save(student);
                courseRepository.save(course);
                return true;

            }
        }
        return false;
    }

    //Student
    @Transactional
    public boolean isEnrolledOnCourse(Integer studentId, Integer courseId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) return false;

        Student student = studentOptional.get();
        // Check if the student actually has a course assigned
        if (student.getCourse() == null) {
            return false; // Not enrolled in any course
        }

        return student.getCourse().getId().equals(courseId);
    }


    //Student
    public boolean markLectureAsRead(Integer studentId, Integer lectureIndex) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) return false;

        Student student = opt.get();

        if (!student.getLecturesRead().containsKey(lectureIndex)) {
            student.getLecturesRead().put(lectureIndex, true);
            studentRepository.save(student);
        }
        return true;
    }

    //Student
    public boolean isLectureRead(Integer studentId, Integer lectureIndex) {
        Optional<Student> opt = studentRepository.findById(studentId);
        return opt.map(student -> student.getLecturesRead().getOrDefault(lectureIndex, false)).orElse(false);
    }

}