package sasha.org.edusmart.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.edusmart.dto.CourseDTO;
import sasha.org.edusmart.dto.FileResourceDTO;
import sasha.org.edusmart.dto.ProfessorDTO;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.model.Course;
import sasha.org.edusmart.model.FileResource;
import sasha.org.edusmart.model.Professor;
import sasha.org.edusmart.model.Student;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.FileResourceRepository;
import sasha.org.edusmart.repo.ProfessorRepository;
import sasha.org.edusmart.repo.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final FileResourceRepository fileResourceRepository;

    public  CourseService(CourseRepository courseRepository, StudentRepository studentRepository,
                          ProfessorRepository professorRepository, FileResourceRepository fileResourceRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.fileResourceRepository = fileResourceRepository;
    }

    //Admin
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(courseDTO.getTitle());
        if (courseOptional.isEmpty()) {
            Course newCourse = Course.of(courseDTO, studentRepository, professorRepository, fileResourceRepository);
            Course savedCourse = courseRepository.save(newCourse);
            return savedCourse.courseDTO();
        }
        return null;
    }

    //Admin
    @Transactional
    public boolean deleteCourse(String title) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(title);
        if (courseOptional.isPresent()) {
            Course courseToDelete = courseOptional.get();

            if (!courseToDelete.getStudents().isEmpty()) {
                List<Student> students = courseToDelete.getStudents();
                for (Student student : students) {
                    student.setCourse(null);
                    studentRepository.save(student);
                }
            }
            if (courseToDelete.getProfessor() != null) {
                Professor professor = courseToDelete.getProfessor();
                professor.setCourse(null);
                professorRepository.save(professor);
            }
            if (courseToDelete.getFiles() != null) {
                List<FileResource> fileResources = courseToDelete.getFiles();
                for (FileResource fileResource : fileResources) {
                    fileResourceRepository.delete(fileResource);
                }
            }
            courseRepository.delete(courseToDelete);
            return true;
        }
        return false;
    }


    @CacheEvict(value = "coursesAll", allEntries = true)
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            return new ArrayList<>();
        }
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for (Course course : courses) {
            courseDTOS.add(course.courseDTO());
        }
        return courseDTOS;
    }


    @Transactional(readOnly = true)
    public CourseDTO getCourseByTitle(String title) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(title);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            CourseDTO courseDTO = course.courseDTO();
            return courseDTO;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getLectureByCourseTitleAndIndex(String title, Integer lectureIndex) {
        return courseRepository.findCourseByTitle(title)
                .map(course -> {
                    List<String> lectures = course.getLectionList();
                    if (lectureIndex != null && lectureIndex >= 0 && lectureIndex < lectures.size()) {
                        return lectures.get(lectureIndex);
                    } else {
                        return null; // invalid index
                    }
                })
                .orElse(null); // course not found
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByCourseTitle(String title) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(title);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            List<Student> students = course.getStudents();
            List<StudentDTO> studentDTOS = new ArrayList<>();
            for (Student student : students) {
                studentDTOS.add(student.studentDTO());
            }
            return studentDTOS;
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public ProfessorDTO getProfessorByCourseTitle(String title) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(title);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            Professor professor = course.getProfessor();
            if (professor != null) {
                return professor.professorDTO();
            } else {
                return null;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<FileResourceDTO> getFilesByCourseTitle(String title) {
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(title);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            List<FileResource> fileResources = course.getFiles();
            List<FileResourceDTO> fileResourceDTOS = new ArrayList<>();
            for (FileResource fileResource : fileResources) {
                fileResourceDTOS.add(fileResource.fileDTO());
            }
            return fileResourceDTOS;
        }
        return new ArrayList<>();
    }
}
