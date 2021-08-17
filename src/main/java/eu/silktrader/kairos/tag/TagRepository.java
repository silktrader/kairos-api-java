package eu.silktrader.kairos.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.silktrader.kairos.user.User;
public interface TagRepository extends JpaRepository<Tag, Long> {
  List<Tag> findByUser(User user);
}
