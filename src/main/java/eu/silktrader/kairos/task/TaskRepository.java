package eu.silktrader.kairos.task;

import eu.silktrader.kairos.user.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByDateInAndUser(List<LocalDate> dates, User user);
}
