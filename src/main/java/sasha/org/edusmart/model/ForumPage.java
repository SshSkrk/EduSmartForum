package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import sasha.org.edusmart.dto.ForumPageDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data @NoArgsConstructor
public class ForumPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String theme;

    @Column(length = 5000)
    private List<String> answers = new ArrayList<>();

    public  ForumPage(String theme) {
        this.theme = theme;
    }

    public static ForumPage of(ForumPageDTO forumPageDTO) {
       ForumPage forumPage = new ForumPage();
       if (forumPageDTO.getId() != null) {
           forumPage.setId(forumPageDTO.getId());
       }
       forumPage.setTheme(forumPageDTO.getTheme());
       if (forumPageDTO.getAnswers() != null) {
           forumPage.setAnswers(forumPageDTO.getAnswers());
       }
        return forumPage;
    }

    public ForumPageDTO forumPageDTO() {
        ForumPageDTO forumPageDTO = new ForumPageDTO();
        forumPageDTO.setId(this.id);
        forumPageDTO.setTheme(this.theme);
        if (answers != null) {
            forumPageDTO.setAnswers(this.answers);
        }
        return forumPageDTO;
    }
}
