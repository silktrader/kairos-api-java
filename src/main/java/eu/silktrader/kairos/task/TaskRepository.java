package eu.silktrader.kairos.task;

import eu.silktrader.kairos.user.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByDateInAndUser(List<LocalDate> dates, User user);

  List<Task> findByUserAndDateIsNull(User user);

  Optional<Task> findByIdAndUserName(Long id, String userName);

  Optional<Task> findByPrevious(Task task);

  Boolean existsByPreviousId(Long id);

  Boolean existsByDateAndPreviousIsNullAndUserName(
    LocalDate date,
    String userName
  );
}
