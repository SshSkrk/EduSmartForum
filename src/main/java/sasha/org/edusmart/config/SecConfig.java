package sasha.org.edusmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sasha.org.edusmart.JWT.JwtAuthFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecConfig {

    private final UserDetailsService userDetailsServiceImpl;
    private final PasswordEncoder encoder;
    private final JwtAuthFilter jwtAuthFilter;

    public SecConfig(UserDetailsService userDetailsServiceImpl, PasswordEncoder encoder, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.encoder = encoder;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Разрешаем фронтенд на localhost:3000
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://edusmart-551269b21410.herokuapp.com"
        ));

        // Разрешаем стандартные методы HTTP
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));

        // Разрешаем любые заголовки (например Authorization для JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Разрешаем отправку cookies / токенов
        configuration.setAllowCredentials(true);

        // Привязываем конфигурацию ко всем URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/index.html", "/login.html","/register.html","/profile.html"
                                , "/course.html","/lecture.html", "/js/**", "/favicon.ico", "/admin.html",
                                "/student.html", "/quiz.html","/professor.html","/forum.html","/theme.html").permitAll()

                        .requestMatchers("/api/current-user").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")


                        //*email*//
                        .requestMatchers("/api/verify").permitAll()
                        .requestMatchers("/api/sendEmail").permitAll()


                        //index
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/logout").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/getAllCourses").permitAll()


                        //register
                        .requestMatchers("/api/student/createPerson").permitAll()


                        //profile
                        .requestMatchers("/api/updatePerson").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")


                        //course
                        .requestMatchers("/api/getCourseByTitle").permitAll()
                        .requestMatchers("/api/getStudentsByCourseTitle").permitAll()
                        .requestMatchers("/api/getProfessorByCourseTitle").permitAll()
                        .requestMatchers("/api/getFilesByCourseTitle").permitAll()

                        .requestMatchers("/api/student/enrollStudentOnCourse/{studentId}/{courseTitle}").hasAnyRole("STUDENT")
                        .requestMatchers("/api/professor/assignProfOnCourse/{professorId}/{courseTitle}").hasAnyRole("PROFESSOR")
                        .requestMatchers("/api/files/upload/**").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/files/download/**").permitAll()


                        //lecture
                        .requestMatchers("/api/getLectureByCourseTitleAndIndex/{studentId}/{lectureIndex}").permitAll()

                        .requestMatchers("/api/student/isEnrolledOnCourse/{studentId}/{courseId}").hasAnyRole("STUDENT")
                        .requestMatchers("/api/student//professor/isAssignedOnCourse/{professorId}/{courseId}").hasAnyRole("STUDENT")

                        .requestMatchers("/api/student/markLectureAsRead/{studentId}/{lectureIndex}").hasAnyRole("STUDENT")
                        .requestMatchers("/api/student/isLectureRead/{studentId}/{lectureIndex}").hasAnyRole("STUDENT")


                        //quiz
                        .requestMatchers("/api/student/courseTitle").hasAnyRole("STUDENT")
                        .requestMatchers("/api/findQuizByCourseTitle").hasAnyRole("STUDENT")

                        .requestMatchers("/api/findQuestionsByQuizTitle").hasAnyRole("STUDENT")

                        .requestMatchers("/api/saveStudentAnswer").hasAnyRole("STUDENT")
                        .requestMatchers("/api/createStudentQuizSubmission").hasAnyRole("STUDENT")
                        .requestMatchers("/api/updateStudentQuizSubmission").hasAnyRole("STUDENT")

                        .requestMatchers("/api/calculateScore").hasAnyRole("STUDENT")


                        //admin
                        .requestMatchers("/api/admin/createPerson").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/deletePerson/{email}").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/getAllPersonList").hasAnyRole("ADMIN")

                        .requestMatchers("/api/admin/createCourse").hasAnyRole("ADMIN")
                        .requestMatchers("/api/admin/deleteCourse/{title}").hasAnyRole("ADMIN")
                        //.requestMatchers("/api/getAllCourses").permitAll()

                        .requestMatchers("/api/getAllSubmissions").hasAnyRole("ADMIN", "PROFESSOR")


                        //student
                        .requestMatchers("/api/getSubmissionByStudentId").hasAnyRole("STUDENT")


                        //professor
                        .requestMatchers("/api/getAllSubmissions").hasAnyRole("ADMIN", "PROFESSOR")

                        //forumPage
                        .requestMatchers("/api/createForumPage").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/searchForumPagesByThemePART").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/getAllForumPages").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/deletePage/{forumPageID}").hasAnyRole("ADMIN")

                        //theme
                        .requestMatchers("/api/addAnswerOnPage/{forumPageID}").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")
                        .requestMatchers("/api/getAllAnswersOfPage").hasAnyRole("ADMIN", "PROFESSOR", "STUDENT")

                        .anyRequest().authenticated()

                ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

