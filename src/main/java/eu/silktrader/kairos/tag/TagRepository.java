package eu.silktrader.kairos.tag;

import eu.silktrader.kairos.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
  List<Tag> findByUser(User user);

  boolean existsByTitle(String title);

  Optional<Tag> findByTitle(String title);

  Optional<Tag> findByIdAndUserName(Long id, String userName);
}
