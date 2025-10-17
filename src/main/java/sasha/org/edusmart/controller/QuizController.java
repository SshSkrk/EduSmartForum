package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.dto.*;
import sasha.org.edusmart.service.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final QuestionServise questionServise;
    private final StudentAnswerService studentAnswerService;
    private final StudentQuizSubmissionService studentQuizSubmissionService;

    public QuizController(QuizService quizService, QuestionServise questionServise,
                          StudentAnswerService studentAnswerService,
                          StudentQuizSubmissionService studentQuizSubmissionService) {
        this.quizService = quizService;
        this.questionServise = questionServise;
        this.studentAnswerService = studentAnswerService;
        this.studentQuizSubmissionService = studentQuizSubmissionService;
    }

    /*@GetMapping("/findAllQuiz")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public List<QuizDTO> findAllQuiz() {
        List<QuizDTO> quizDTOS = quizService.findAllQuiz();
        if (quizDTOS.isEmpty()) {
            return new ArrayList<>();
        }
        return quizDTOS;

    }
     */

    @GetMapping("/findQuizByCourseTitle")
    @PreAuthorize("hasRole('STUDENT')")
    public QuizDTO findQuizByCourseTitle(@RequestParam String courseTitle) {
        QuizDTO quizDTO = quizService.findQuizByCourseTitle(courseTitle);
        return quizDTO;
    }

    @GetMapping("/findQuestionsByQuizTitle")
    @PreAuthorize("hasRole('STUDENT')")
    public List<QuestionDTO> findQuestionsByQuizTitle(@RequestParam String quizTitle) {
        List<QuestionDTO> questionDTOS = questionServise.findQuestionsByQuizTitle(quizTitle);
        return questionDTOS;
    }


    @PostMapping("/saveStudentAnswer")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAnswerDTO> saveStudentAnswer(@RequestBody StudentAnswerDTO dto) {
        StudentAnswerDTO savedDTO = studentAnswerService.saveStudentAnswer(dto);
        return ResponseEntity.ok(savedDTO);
    }


    @PostMapping("/createStudentQuizSubmission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createStudentQuizSubmission(@RequestBody StudentQuizSubmissionDTO dto) {
        StudentQuizSubmissionDTO studentQuizSubmissionDTO = studentQuizSubmissionService.createStudentQuizSubmission(dto);
        if (studentQuizSubmissionDTO != null) {
            return ResponseEntity.ok(Map.of("id", studentQuizSubmissionDTO.getId()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Submission already exists"));
    }

    @PostMapping("/updateStudentQuizSubmission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudentQuizSubmission(@RequestBody StudentQuizSubmissionDTO dto) {
        try {
            StudentQuizSubmissionDTO updatedDTO = studentQuizSubmissionService.updateStudentQuizSubmission(dto);

            if (updatedDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Submission not found"));
            }

            List<Integer> answerIds = updatedDTO.getStudentAnswerDTOS() != null
                    ? updatedDTO.getStudentAnswerDTOS().stream()
                    .map(StudentAnswerDTO::getId)
                    .filter(Objects::nonNull)
                    .toList()
                    : List.of();

            return ResponseEntity.ok(Map.of(
                    "id", updatedDTO.getId(),
                    "quizTitle", updatedDTO.getQuizTitle(),
                    "score", updatedDTO.getScore(),
                    "answersId", answerIds
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/getAllSubmissions")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public List<StudentQuizSubmissionDTO> getAllSubmissions() {
        List<StudentQuizSubmissionDTO> studentQuizSubmissionDTOS = studentQuizSubmissionService.getAllSubmissions();
        return studentQuizSubmissionDTOS;
    }


    @GetMapping("/getSubmissionByStudentId")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentQuizSubmissionDTO getSubmissionByStudentId(@RequestParam Integer studentId) {
        StudentQuizSubmissionDTO studentQuizSubmissionDTO = studentQuizSubmissionService.getSubmissionByStudentId(studentId);
        return studentQuizSubmissionDTO;
    }


    @GetMapping("/calculateScore")
    @PreAuthorize("hasRole('STUDENT')")
    public Integer calculateScore(@RequestParam Integer submissionId) {
        Integer score = studentQuizSubmissionService.calculateScore(submissionId);
        return score;
    }
}
