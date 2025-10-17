package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import sasha.org.edusmart.dto.StudentAnswerDTO;
import sasha.org.edusmart.dto.StudentQuizSubmissionDTO;
import sasha.org.edusmart.model.*;
import sasha.org.edusmart.repo.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentQuizSubmissionService {

    private final StudentQuizSubmissionRepository studentQuizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;

    public StudentQuizSubmissionService(StudentQuizSubmissionRepository studentQuizSubmissionRepository,
                                        QuizRepository quizRepository, StudentAnswerRepository studentAnswerRepository,
                                        StudentRepository studentRepository, QuestionRepository questionRepository) {
        this.studentQuizSubmissionRepository = studentQuizSubmissionRepository;
        this.quizRepository = quizRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
    }

    /** Create a new student quiz submission */
    public StudentQuizSubmissionDTO createStudentQuizSubmission(StudentQuizSubmissionDTO dto) {

        System.out.println(dto.toString()+ " createStudentQuizSubmission8888888888888");
        // 1️⃣ Ensure the student exists
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + dto.getStudentId()));

        // 2️⃣ Ensure the quiz exists
        Quiz quiz = quizRepository.findByTitle(dto.getQuizTitle())
                .orElseThrow(() -> new RuntimeException("Quiz not found with title: " + dto.getQuizTitle()));

        // 3️⃣ Check if submission already exists for this student & quiz
        boolean exists = studentQuizSubmissionRepository.existsByStudentAndQuiz(student, quiz);
        if (exists) {
            throw new RuntimeException("Submission already exists for student " + student.getId() + " and quiz " + quiz.getTitle());
        }

        // 4️⃣ Create and save submission
        StudentQuizSubmission submission = new StudentQuizSubmission();
        submission.setStudent(student);
        submission.setQuiz(quiz);
        submission.setAnswers(new ArrayList<>()); // start with empty answers
        submission.setScore(dto.getScore());

        StudentQuizSubmission saved = studentQuizSubmissionRepository.save(submission);

        // 5️⃣ Convert to DTO and return
        return saved.studentQuizSubmissionDTO();
    }

    /** Update a new student quiz submission */
    public StudentQuizSubmissionDTO updateStudentQuizSubmission(StudentQuizSubmissionDTO dto) {
        StudentQuizSubmission submission = studentQuizSubmissionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // ✅ Update student safely
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        submission.setStudent(student);

        // ✅ Update quiz safely
        Quiz quiz = quizRepository.findByTitle(dto.getQuizTitle())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        submission.setQuiz(quiz);

        // ✅ Update answers safely (avoid replacing the collection)
        List<StudentAnswer> managedAnswers = submission.getAnswers();
        managedAnswers.clear(); // clear in-place; keeps same Hibernate-managed collection

        if (dto.getStudentAnswerDTOS() != null && !dto.getStudentAnswerDTOS().isEmpty()) {
            for (StudentAnswerDTO ansDTO : dto.getStudentAnswerDTOS()) {
                Question question = questionRepository.findByText(ansDTO.getQuestionText())
                        .orElseThrow(() -> new RuntimeException("Question not found: " + ansDTO.getQuestionText()));

                StudentAnswer ans;
                if (ansDTO.getId() != null) {
                    ans = studentAnswerRepository.findById(ansDTO.getId()).orElse(new StudentAnswer());
                } else {
                    ans = new StudentAnswer();
                }

                ans.setStudentQuizSubmission(submission);
                ans.setAnswer(ansDTO.getAnswer());
                ans.setCorrect(ansDTO.isCorrect());
                ans.setQuestion(question);

                managedAnswers.add(ans);
            }
        }

        submission.setScore(dto.getScore());
        StudentQuizSubmission saved = studentQuizSubmissionRepository.save(submission);

        return saved.studentQuizSubmissionDTO();
    }


    /** Get all submissions */
    public List< StudentQuizSubmissionDTO> getAllSubmissions() {
        List<StudentQuizSubmission> studentQuizSubmissions = studentQuizSubmissionRepository.findAll();
        List<StudentQuizSubmissionDTO> studentQuizSubmissionDTOS = new ArrayList<>();
        studentQuizSubmissions.stream().map(sqs -> sqs.studentQuizSubmissionDTO()).forEach(studentQuizSubmissionDTOS::add);
        return studentQuizSubmissionDTOS;
    }

    /** Get a student’s submission by student ID */
    public StudentQuizSubmissionDTO getSubmissionByStudentId(Integer studentId) {
        return studentQuizSubmissionRepository.findByStudent_Id(studentId)
                .map(StudentQuizSubmission::studentQuizSubmissionDTO)
                .orElse(null);
    }


    /** Grade a submission and calculate score */
    public Integer calculateScore(Integer submissionId) {
        StudentQuizSubmission submission = studentQuizSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        int correctCount = 0;
        List<StudentAnswer> answers = submission.getAnswers();

        for (StudentAnswer answer : answers) {
            Question question = answer.getQuestion();
            if (question.getCorrectAnswer().equalsIgnoreCase(answer.getAnswer().trim())) {
                answer.setCorrect(true);
                correctCount++;
            } else {
                answer.setCorrect(false);
            }
        }

        submission.setScore(correctCount); // store number of correct answers
        studentQuizSubmissionRepository.save(submission);

        return correctCount; // returns 0..questions.size()
    }

}
