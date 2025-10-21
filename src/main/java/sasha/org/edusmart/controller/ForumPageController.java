package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.dto.ForumPageDTO;
import sasha.org.edusmart.model.ForumPage;
import sasha.org.edusmart.service.ForumPageService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ForumPageController {
    ForumPageService forumPageService;

    public ForumPageController(ForumPageService forumPageService) {
        this.forumPageService = forumPageService;
    }

    @PostMapping(value = "/createForumPage")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public ResponseEntity<?> createForumPage(@RequestBody String theme) {
        ForumPage forumPage = new ForumPage();
        forumPage.setTheme(theme);
        boolean created = forumPageService.createForumPage(forumPage.forumPageDTO());
        return created
                ? ResponseEntity.status(HttpStatus.CREATED).body("ForumPage created successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create ForumPage");
    }

    @PostMapping(value = "/addAnswerOnPage/{forumPageID}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public ResponseEntity<?> addAnswerOnPage(@PathVariable("forumPageID") Integer forumPageID, @RequestBody String answer) {
        boolean added = forumPageService.addAnswerOnPage(forumPageID, answer);
        return added
                ? ResponseEntity.status(HttpStatus.CREATED).body("Added answer successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add answer");
    }

    @GetMapping("/getAllAnswersOfPage")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public List<String> getAllAnswersOfPage(@RequestParam  Integer forumPageID) {
        List<String> answers = forumPageService.getAllAnswersOfPage(forumPageID);
        if (answers.isEmpty()) {
            return new ArrayList<>();
        }
        return answers;
    }

    @GetMapping("/searchForumPagesByThemePART")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public List<ForumPageDTO> searchForumPagesByThemePART(@RequestParam  String theme) {
        List<ForumPageDTO> pageDTOS = forumPageService.searchForumPagesByThemePART(theme);
        if (pageDTOS.isEmpty()) {
            return new ArrayList<>();
        }
        return pageDTOS;
    }

    @GetMapping("/getAllForumPages")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public List<ForumPageDTO> getAllForumPages() {
        List<ForumPageDTO> pageDTOS = forumPageService.getAllForumPages();
        if (pageDTOS.isEmpty()) {
            return new ArrayList<>();
        }
        return pageDTOS;
    }

    //ADMIN
    @DeleteMapping("/deletePage/{forumPageID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deletePage(@PathVariable("forumPageID") Integer forumPageID) {
        boolean deleted = forumPageService.deletePage(forumPageID);
        return deleted
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

}