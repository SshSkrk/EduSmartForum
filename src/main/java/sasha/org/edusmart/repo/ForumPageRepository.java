package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sasha.org.edusmart.model.ForumPage;

import java.util.List;

public interface ForumPageRepository extends JpaRepository<ForumPage, Integer> {
    List<ForumPage> findByThemeContainingIgnoreCase(String theme);

}
