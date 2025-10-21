package sasha.org.edusmart.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sasha.org.edusmart.dto.CourseDTO;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.model.*;
import sasha.org.edusmart.repo.FileResourceRepository;
import sasha.org.edusmart.repo.ProfessorRepository;
import sasha.org.edusmart.repo.StudentRepository;
import sasha.org.edusmart.service.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*") // replace with your frontend URL if needed
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(final PersonService personService,
                                  final CourseService courseService,
                                  final QuizService quizService,
                                  final QuestionServise questionServise,
                                  final StudentRepository studentRepository,
                                  final ProfessorRepository professorRepository,
                                  final FileResourceRepository fileResourceRepository,
                                  final ForumPageService forumPageService) {
        return args -> {

            // ✅ Create persons only if they don't exist
            if (personService.getPersonByEmail("skorykoleksandra@gmail.com") == null) {
                Person person1 = new Person();
                person1.setEmail("skorykoleksandra@gmail.com");
                person1.setVerified(false);
                person1.setPassword("123456");
                person1.setRole("ADMIN");
                person1.setFirstName("Oleksandra");
                person1.setLastName("Skoryk");
                PersonDTO personDTO1 = personService.createPerson(person1.personDTO());
            }

            if (personService.getPersonByEmail("so@gmail.com") == null) {
                Person person2 = new Person();
                person2.setEmail("so@gmail.com");
                person2.setVerified(true);
                person2.setPassword("123456");
                person2.setRole("STUDENT");
                person2.setFirstName("SO");
                person2.setLastName("Skoryk");
                PersonDTO personDTO2 = personService.createPerson(person2.personDTO());
            }

            if (personService.getPersonByEmail("os@gmail.com") == null) {
                Person person3 = new Person();
                person3.setEmail("os@gmail.com");
                person3.setVerified(true);
                person3.setPassword("123456");
                person3.setRole("PROFESSOR");
                person3.setFirstName("OS");
                person3.setLastName("Skoryk");
                PersonDTO personDTO3 = personService.createPerson(person3.personDTO());
            }


            // ✅ Create courses only if they don't exist
            if (courseService.getCourseByTitle("Java course") == null) {
                Course javaCourse = new Course();
                javaCourse.setTitle("Java course");
                javaCourse.setDescription("2025-2026");

                List<String> lectures = List.of(
                        "Introduction to Java and JVM",
                        "Setting Up Java Development Environment",
                        "Java Syntax and Basic Structure",
                        "Data Types and Variables",
                        "Operators and Expressions",
                        "Control Flow: If, Switch, Loops",
                        "Methods and Parameter Passing",
                        "Object-Oriented Programming Basics",
                        "Classes and Objects",
                        "Constructors and Overloading",
                        "Encapsulation and Access Modifiers",
                        "Inheritance and Polymorphism",
                        "Abstract Classes and Interfaces",
                        "Packages and Import Statements",
                        "Exception Handling",
                        "Collections Framework: List, Set, Map",
                        "Generics and Type Safety",
                        "Streams and Lambda Expressions",
                        "File I/O and Serialization",
                        "Introduction to Multithreading and Concurrency"
                );
                javaCourse.setLectionList(new ArrayList<>(lectures));
                CourseDTO courseDTO1 = courseService.createCourse(javaCourse.courseDTO());
                Course course1 = Course.of(courseDTO1,studentRepository, professorRepository,fileResourceRepository);


                Quiz quiz = new Quiz("Java Test", "Short general test",course1);
                List<Question> questions = List.of(
                        new Question(
                                "What is JVM in Java?",
                                "Java Virtual Machine", // correct answer
                                List.of("Java Variable Machine", "Java Virtual Machine", "Just Virtual Machine", "Java Verified Manager"), // all options
                                quiz
                        ),
                        new Question(
                                "Which keyword is used to inherit a class in Java?",
                                "extends",
                                List.of("implement", "extends", "inherits", "super"),
                                quiz
                        ),
                        new Question(
                                "What is the default value of a boolean variable in Java?",
                                "false",
                                List.of("true", "false", "null", "0"),
                                quiz
                        ),
                        new Question(
                                "Which of these is not a Java primitive type?",
                                "String",
                                List.of("int", "float", "String", "boolean"),
                                quiz
                        ),
                        new Question(
                                "Which method is called when an object is created?",
                                "constructor",
                                List.of("start()", "main()", "constructor", "create()"),
                                quiz
                        ),
                        new Question(
                                "Which collection class allows duplicate elements?",
                                "List",
                                List.of("Set", "Map", "List", "None of the above"),
                                quiz
                        ),
                        new Question(
                                "What is the size of int in Java?",
                                "32 bits",
                                List.of("8 bits", "16 bits", "32 bits", "64 bits"),
                                quiz
                        ),
                        new Question(
                                "Which of these is used to handle exceptions in Java?",
                                "try-catch",
                                List.of("try-catch", "if-else", "for loop", "switch"),
                                quiz
                        ),
                        new Question(
                                "Which operator is used for comparison in Java?",
                                "==",
                                List.of("=", "==", "equals", "==="),
                                quiz
                        ),
                        new Question(
                                "Which keyword prevents a method from being overridden?",
                                "final",
                                List.of("static", "final", "const", "private"),
                                quiz
                        )
                );
                quiz.setQuestions(questions);

                quizService.createQuizWithQuestions(quiz);

                //questions.stream().map(question -> question.questionDTO()).
                        //forEach(questionServise::createQuestion);
                //quizService.createQuiz(quiz.quizDTO());





            }



            if (courseService.getCourseByTitle("Python course") == null) {
                Course pythonCourse = new Course();
                pythonCourse.setTitle("Python course");
                pythonCourse.setDescription("2025-2026");

                List<String> lectures = List.of(
                        "Introduction to Python and Installation",
                        "Understanding Python Syntax and Variables",
                        "Data Types and Type Conversion",
                        "Control Flow: if, elif, else",
                        "Loops: for and while",
                        "Functions and Scope",
                        "Modules and Packages",
                        "Lists, Tuples, and Sets",
                        "Dictionaries and Comprehensions",
                        "File Handling and Context Managers",
                        "Error and Exception Handling",
                        "Object-Oriented Programming in Python",
                        "Classes and Inheritance",
                        "Decorators and Generators",
                        "Working with Virtual Environments",
                        "Introduction to Libraries: NumPy and Pandas",
                        "Data Visualization with Matplotlib",
                        "Working with APIs and JSON",
                        "Introduction to Django and Flask",
                        "Final Project: Build a Mini Application"
                );
                pythonCourse.setLectionList(new ArrayList<>(lectures));
                CourseDTO courseDTO2 = courseService.createCourse(pythonCourse.courseDTO());
            }

            if (forumPageService.searchForumPage("How to create your first website on java") == null) {
                ForumPage forumPage1 = new ForumPage("How to create your first website on java");
                forumPageService.createForumPage(forumPage1.forumPageDTO());
            }

            if (forumPageService.searchForumPage("Help python question") == null) {
            ForumPage forumPage2 = new ForumPage("Help python question");
            forumPageService.createForumPage(forumPage2.forumPageDTO());
            }


            if (forumPageService.searchForumPage("Searching for frontend dev") == null) {
            ForumPage forumPage3 = new ForumPage("Searching for frontend dev");
            forumPageService.createForumPage(forumPage3.forumPageDTO());
            }

        };
    }
}
