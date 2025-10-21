package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.edusmart.dto.ForumPageDTO;
import sasha.org.edusmart.model.ForumPage;
import sasha.org.edusmart.repo.ForumPageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumPageService {
    private final ForumPageRepository forumPageRepository;

    public ForumPageService(ForumPageRepository forumPageRepository) {
        this.forumPageRepository = forumPageRepository;
    }

    @Transactional
    public boolean createForumPage(ForumPageDTO forumPageDTO) {
        ForumPage forumPage = ForumPage.of(forumPageDTO);
        forumPageRepository.save(forumPage);
        return true;
    }

    @Transactional
    public boolean addAnswerOnPage(Integer forumPageID, String answer) {
        ForumPage forumPage = forumPageRepository.findById(forumPageID).orElse(null);
        if (forumPage == null) {
            return false;
        }
        forumPage.getAnswers().add(answer);
        forumPageRepository.save(forumPage);
        return true;
    }


    @Transactional(readOnly = true)
    public List<String> getAllAnswersOfPage(Integer forumPageID) {
        ForumPage forumPage = forumPageRepository.findById(forumPageID).orElse(null);
        if (forumPage == null) {
            return new ArrayList<>();
        }
        List<String> answers = forumPage.getAnswers();
        if (answers == null) {
            return new ArrayList<>();
        }
        return answers;
    }

    @Transactional(readOnly = true)
    public List<ForumPageDTO> searchForumPagesByThemePART(String theme) {
        List<ForumPage> forumPages = forumPageRepository.findByThemeContainingIgnoreCase(theme);
        return forumPages.stream().map(ForumPage::forumPageDTO).collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public List<ForumPageDTO> getAllForumPages() {
        List<ForumPage> forumPages = forumPageRepository.findAll();
        if (forumPages.isEmpty()) {
            return null;
        }
        List<ForumPageDTO> forumPageDTOS = forumPages.stream().map(ForumPage::forumPageDTO).collect(Collectors.toList());
        return forumPageDTOS;
    }

    //ADMIN
    @Transactional
    public boolean deletePage(Integer forumPageID) {
        Optional<ForumPage> forumPageOptional = forumPageRepository.findById(forumPageID);
        if (forumPageOptional.isPresent()) {
            forumPageRepository.delete(forumPageOptional.get());
            return true;
        }
        return false;
    }

}
